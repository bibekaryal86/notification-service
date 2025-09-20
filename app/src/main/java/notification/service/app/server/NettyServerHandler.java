package notification.service.app.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.Attribute;
import java.util.Objects;
import notification.service.app.controller.AppTestController;
import notification.service.app.controller.EmailController;
import notification.service.app.repository.EmailRecordRepository;
import notification.service.app.service.EmailService;
import notification.service.app.util.Constants;
import notification.service.app.util.ServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

  private final AppTestController appTestController;
  private final EmailController emailController;

  public NettyServerHandler(
      final EmailService emailService, final EmailRecordRepository emailRecordRepository) {
    this.appTestController = new AppTestController();
    this.emailController = new EmailController(emailService, emailRecordRepository);
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
    if (msg instanceof FullHttpRequest fullHttpRequest) {
      final String requestId = ctx.channel().attr(Constants.REQUEST_ID).get();
      final String requestedId = getRequestedId(ctx);
      if (Objects.equals(requestId, requestedId)) {
        log.debug("[{}] Request is Requested...", requestId);
        return;
      }

      final HttpMethod httpMethod = fullHttpRequest.method();
      final String requestUriLessParams =
          ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());

      try {
        if (requestUriLessParams.startsWith(Constants.APP_TESTS_CONTROLLER)) {
          log.info("[{}] Routing to AppTestController...", requestId);
          appTestController.handleRequest(ctx, requestUriLessParams, httpMethod);
        } else if (requestUriLessParams.startsWith(Constants.EMAIL_API_CONTROLLER)) {
          log.info("[{}] Routing to EmailController...", requestId);
          emailController.handleRequest(
              ctx, fullHttpRequest, requestUriLessParams, httpMethod, requestId);
        } else {
          ServerUtils.sendResponse(
              ctx, "Servlet Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
        }
      } finally {
        setRequestedId(ctx, requestId);
      }
    } else {
      super.channelRead(ctx, msg);
    }
  }

  @Override
  public void exceptionCaught(
      final ChannelHandlerContext channelHandlerContext, final Throwable throwable) {
    final String requestId = channelHandlerContext.channel().attr(Constants.REQUEST_ID).get();
    log.error("[{}] Servlet Handler Exception Caught...", requestId, throwable);

    ServerUtils.sendResponse(
        channelHandlerContext,
        "Servlet Handler Exception Caught: " + throwable.getMessage(),
        HttpResponseStatus.INTERNAL_SERVER_ERROR);
  }

  private String getRequestedId(final ChannelHandlerContext ctx) {
    Attribute<String> attribute = ctx.channel().attr(Constants.REQUESTED_ID);
    return attribute == null ? null : attribute.getAndSet("");
  }

  private void setRequestedId(final ChannelHandlerContext ctx, final String requestId) {
    ctx.channel().attr(Constants.REQUESTED_ID).set(requestId);
  }
}
