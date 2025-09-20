package notification.service.app.controller;

import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import notification.service.app.model.EmailRecordEntity;
import notification.service.app.model.EmailRequest;
import notification.service.app.repository.EmailRecordRepository;
import notification.service.app.service.EmailService;
import notification.service.app.util.Constants;
import notification.service.app.util.ServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailController {
  private static final Logger log = LoggerFactory.getLogger(EmailController.class);

  private final EmailService emailService;
  private final EmailRecordRepository emailRecordRepository;

  public EmailController(
      final EmailService emailService, final EmailRecordRepository emailRecordRepository) {
    this.emailService = emailService;
    this.emailRecordRepository = emailRecordRepository;
  }

  public void handleRequest(
      final ChannelHandlerContext ctx,
      final FullHttpRequest fullHttpRequest,
      final String requestUriLessParams,
      final HttpMethod httpMethod,
      final String requestId) {

    switch (requestUriLessParams) {
      case Constants.SEND_EMAIL -> {
        if (httpMethod.equals(HttpMethod.POST)) {
          handleSendEmailRequest(ctx, fullHttpRequest, requestId);
        } else {
          ServerUtils.sendResponseMethodNotAllowed(ctx);
        }
      }
      case Constants.FIND_EMAIL -> {
        if (httpMethod.equals(HttpMethod.GET)) {
          handleFindEmailRequest(ctx, fullHttpRequest, requestId);
        } else {
          ServerUtils.sendResponseMethodNotAllowed(ctx);
        }
      }
      case null, default ->
          ServerUtils.sendResponse(
              ctx, "EmailController Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
    }
  }

  private void handleSendEmailRequest(
      final ChannelHandlerContext ctx,
      final FullHttpRequest fullHttpRequest,
      final String requestId) {
    final LocalDateTime receivedAt = LocalDateTime.now();
    final EmailRequest emailRequest =
        ServerUtils.getRequestBody(fullHttpRequest, EmailRequest.class);
    if (validateEmailRequest(emailRequest)) {
      final EmailRecordEntity emailRecordEntity =
          createEmailRecordEntity(requestId, emailRequest, receivedAt);
      emailRecordRepository.save(emailRecordEntity);
      ServerUtils.sendResponse(
          ctx,
          null,
          HttpResponseStatus.ACCEPTED,
          String.format(Constants.SIMPLE_JSON_RESPONSE, "requestId", requestId));

      CompletableFuture.runAsync(
          () -> {
            try {
              emailService.sendEmail(emailRequest);
              emailRecordRepository.update(requestId, LocalDateTime.now(), "");
              log.info("[{}] Email sent successfully", requestId);
            } catch (Exception ex) {
              emailRecordRepository.update(requestId, LocalDateTime.now(), ex.getMessage());
              log.error("[{}] Failed to send email", requestId, ex);
            }
          });
    } else {
      log.error("[{}] EmailRequest not found or invalid...", requestId);
      ServerUtils.sendResponse(
          ctx, "EmailRequest not found or invalid...", HttpResponseStatus.BAD_REQUEST);
    }
  }

  private void handleFindEmailRequest(
      final ChannelHandlerContext ctx,
      final FullHttpRequest fullHttpRequest,
      final String currentRequestId) {
    final String requestId = ServerUtils.getQueryParam(fullHttpRequest.uri(), "requestId", "");

    if (CommonUtilities.isEmpty(requestId)) {
      ServerUtils.sendResponse(
          ctx, "RequestId not found or invalid...", HttpResponseStatus.BAD_REQUEST);
    } else {
      final EmailRecordEntity emailRecordEntity =
          emailRecordRepository.find(requestId, currentRequestId);

      if (emailRecordEntity == null) {
        ServerUtils.sendResponse(
            ctx, "Email Record Entity Not Found by RequestId", HttpResponseStatus.NOT_FOUND);
      } else {
        ServerUtils.sendResponse(ctx, emailRecordEntity, HttpResponseStatus.OK, "");
      }
    }
  }

  private EmailRecordEntity createEmailRecordEntity(
      final String requestId, final EmailRequest emailRequest, final LocalDateTime receivedAt) {
    final String emailFrom = CommonUtilities.getSystemEnvProperty(Constants.ENV_SMTP_EMAIL);
    final String emailTo = String.join(",", emailRequest.getEmailRequestRecipients().getEmailTo());
    final String emailCc =
        CommonUtilities.isEmpty(emailRequest.getEmailRequestRecipients().getEmailCc())
            ? ""
            : String.join(",", emailRequest.getEmailRequestRecipients().getEmailCc());
    final String emailBcc =
        CommonUtilities.isEmpty(emailRequest.getEmailRequestRecipients().getEmailCc())
            ? ""
            : String.join(",", emailRequest.getEmailRequestRecipients().getEmailBcc());
    boolean hasHtmlBody = !CommonUtilities.isEmpty(emailRequest.getHtmlBody());
    boolean hasTextBody = !CommonUtilities.isEmpty(emailRequest.getTextBody());
    boolean hasAttachments = !CommonUtilities.isEmpty(emailRequest.getEmailRequestAttachments());

    return new EmailRecordEntity(
        requestId,
        emailRequest.getSubject(),
        hasHtmlBody,
        hasTextBody,
        hasAttachments,
        emailFrom,
        emailTo,
        emailCc,
        emailBcc,
        receivedAt,
        null,
        null);
  }

  private boolean validateEmailRequest(final EmailRequest emailRequest) {
    if (emailRequest == null) {
      return false;
    }
    if (CommonUtilities.isEmpty(emailRequest.getSubject())
        || (CommonUtilities.isEmpty(emailRequest.getTextBody())
            && CommonUtilities.isEmpty(emailRequest.getHtmlBody()))) {
      return false;
    }
    return emailRequest.getEmailRequestRecipients() != null
        && !CommonUtilities.isEmpty(emailRequest.getEmailRequestRecipients().getEmailTo());
  }
}
