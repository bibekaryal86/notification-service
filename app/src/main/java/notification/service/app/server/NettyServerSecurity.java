package notification.service.app.server;

import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Objects;
import notification.service.app.util.Constants;
import notification.service.app.util.ServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerSecurity extends ChannelDuplexHandler {
  private static final Logger log = LoggerFactory.getLogger(NettyServerSecurity.class);

  @Override
  public void channelRead(final ChannelHandlerContext channelHandlerContext, final Object object)
      throws Exception {
    if (object instanceof FullHttpRequest fullHttpRequest) {
      final String requestUriLessParams =
          ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());
      final String requestId = channelHandlerContext.channel().attr(Constants.REQUEST_ID).get();

      if (isExcludedFromAuth(requestUriLessParams)) {
        log.debug("[{}] Excluded From Authorization: [{}]", requestId, requestUriLessParams);
        super.channelRead(channelHandlerContext, fullHttpRequest);
        return;
      }

      String authHeader = extractAuthHeader(fullHttpRequest);
      if (CommonUtilities.isEmpty(authHeader)) {
        logAndReject(channelHandlerContext, requestId, requestUriLessParams, "Not Authorized...");
        return;
      }

      if (!isAuthenticated(authHeader)) {
        logAndReject(
            channelHandlerContext, requestId, requestUriLessParams, "Not Authenticated...");
        return;
      }

      super.channelRead(channelHandlerContext, object);
    }

    super.channelRead(channelHandlerContext, object);
  }

  private boolean isExcludedFromAuth(String requestUri) {
    return Constants.APP_TESTS_PING.equals(requestUri);
  }

  private String extractAuthHeader(FullHttpRequest request) {
    String authHeader = request.headers().get(HttpHeaderNames.AUTHORIZATION);
    if (CommonUtilities.isEmpty(authHeader)) {
      authHeader = request.headers().get(HttpHeaderNames.AUTHORIZATION.toString().toLowerCase());
    }
    return authHeader;
  }

  private void logAndReject(
      ChannelHandlerContext ctx, String requestId, String requestUri, String message) {
    log.warn("[{}] {}: [{}]", requestId, message, requestUri);
    ServerUtils.sendResponse(ctx, message, HttpResponseStatus.UNAUTHORIZED);
  }

  private boolean isAuthenticated(final String actualAuth) {
    final String username = CommonUtilities.getSystemEnvProperty(Constants.ENV_SELF_USER);
    final String password = CommonUtilities.getSystemEnvProperty(Constants.ENV_SELF_PWD);
    final String expectedAuth = CommonUtilities.getBasicAuth(username, password);
    return Objects.equals(expectedAuth, actualAuth);
  }
}
