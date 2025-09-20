package notification.service.app.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import notification.service.app.model.ServerConfig;
import notification.service.app.repository.EmailRecordRepository;
import notification.service.app.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
  private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

  private final ServerConfig config;
  private final EmailService emailService;
  private final EmailRecordRepository emailRecordRepository;

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ChannelFuture serverChannelFuture;

  public NettyServer(
      final ServerConfig config,
      final EmailService emailService,
      final EmailRecordRepository emailRecordRepository) {
    this.config = config;
    this.emailService = emailService;
    this.emailRecordRepository = emailRecordRepository;
  }

  public void start() {
    bossGroup = new NioEventLoopGroup(config.bossThreads());
    workerGroup = new NioEventLoopGroup(config.workerThreads());

    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, config.soBacklog())
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.TCP_NODELAY, true)
          .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.connectTimeoutMillis())
          .childHandler(createChannelInitializer());

      serverChannelFuture = serverBootstrap.bind(config.port()).sync();
      log.info("Server started on port {}", config.port());
    } catch (Exception e) {
      stop();
      throw new RuntimeException("Failed to start Netty server", e);
    }
  }

  private ChannelInitializer<SocketChannel> createChannelInitializer() {
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast(new HttpServerCodec())
            .addLast(new HttpObjectAggregator(config.maxContentLength()))
            .addLast(newCorsHandler())
            .addLast(new NettyServerLogging())
            .addLast(new NettyServerSecurity())
            .addLast(new NettyServerHandler(emailService, emailRecordRepository));
      }
    };
  }

  public void stop() {
    log.info("Shutting down Netty server...");

    if (serverChannelFuture != null) {
      serverChannelFuture.channel().close();
    }
    if (workerGroup != null) {
      workerGroup.shutdownGracefully();
    }
    if (bossGroup != null) {
      bossGroup.shutdownGracefully();
    }

    log.info("Netty server stopped");
  }

  public void awaitTermination() throws InterruptedException {
    if (serverChannelFuture != null) {
      serverChannelFuture.channel().closeFuture().sync();
    }
  }

  public CorsHandler newCorsHandler() {
    return new CorsHandler(
        CorsConfigBuilder.forAnyOrigin()
            .allowedRequestMethods(HttpMethod.GET, HttpMethod.POST)
            .allowedRequestHeaders(
                HttpHeaderNames.AUTHORIZATION,
                HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderNames.CONTENT_LENGTH)
            .build());
  }
}
