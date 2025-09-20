package notification.service.app.controller;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import notification.service.app.util.Constants;
import notification.service.app.util.ServerUtils;

public class AppTestController {

  public void handleRequest(
      final ChannelHandlerContext ctx,
      final String requestUriLessParams,
      final HttpMethod httpMethod) {

    switch (requestUriLessParams) {
      case Constants.APP_TESTS_PING -> {
        if (httpMethod.equals(HttpMethod.GET)) {
          ServerUtils.sendResponse(
              ctx,
              null,
              HttpResponseStatus.OK,
              String.format(Constants.SIMPLE_JSON_RESPONSE, "ping", "successful"));
        } else {
          ServerUtils.sendResponseMethodNotAllowed(ctx);
        }
      }
      case null, default ->
          ServerUtils.sendResponse(
              ctx, "AppTestController Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
    }
  }
}
