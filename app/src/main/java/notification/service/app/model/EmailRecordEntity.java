package notification.service.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EmailRecordEntity implements Serializable {
  private final String id;
  private final String subject;
  private final boolean hasHtmlBody;
  private final boolean hasTextBody;
  private final boolean hasAttachments;
  private final String emailFrom;
  private final String emailTo;
  private final String emailCc;
  private final String emailBcc;
  private final LocalDateTime receivedAt;
  private final LocalDateTime sentAt;
  private final String errorMessage;

  public EmailRecordEntity(
      final String requestId,
      final String subject,
      final boolean hasHtmlBody,
      final boolean hasTextBody,
      final boolean hasAttachments,
      final String emailFrom,
      final String emailTo,
      final String emailCc,
      final String emailBcc,
      final LocalDateTime receivedAt,
      final LocalDateTime sentAt,
      final String errorMessage) {
    this.id = requestId;
    this.subject = subject;
    this.hasHtmlBody = hasHtmlBody;
    this.hasTextBody = hasTextBody;
    this.hasAttachments = hasAttachments;
    this.emailFrom = emailFrom;
    this.emailTo = emailTo;
    this.emailCc = emailCc;
    this.emailBcc = emailBcc;
    this.receivedAt = receivedAt;
    this.sentAt = sentAt;
    this.errorMessage = errorMessage;
  }

  public String getId() {
    return id;
  }

  public String getSubject() {
    return subject;
  }

  public boolean isHasHtmlBody() {
    return hasHtmlBody;
  }

  public boolean isHasTextBody() {
    return hasTextBody;
  }

  public boolean isHasAttachments() {
    return hasAttachments;
  }

  public String getEmailFrom() {
    return emailFrom;
  }

  public String getEmailTo() {
    return emailTo;
  }

  public String getEmailCc() {
    return emailCc;
  }

  public String getEmailBcc() {
    return emailBcc;
  }

  public LocalDateTime getReceivedAt() {
    return receivedAt;
  }

  public LocalDateTime getSentAt() {
    return sentAt;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public String toString() {
    return "EmailRecord{"
        + "id="
        + id
        + ", subject='"
        + subject
        + '\''
        + ", hasHtmlBody="
        + hasHtmlBody
        + ", hasTextBody="
        + hasTextBody
        + ", hasAttachments="
        + hasAttachments
        + ", emailFrom='"
        + emailFrom
        + '\''
        + ", emailTo='"
        + emailTo
        + '\''
        + ", emailCc='"
        + emailCc
        + '\''
        + ", emailBcc='"
        + emailBcc
        + '\''
        + ", receivedAt="
        + receivedAt
        + ", sentAt="
        + sentAt
        + ", errorMessage='"
        + errorMessage
        + '\''
        + '}';
  }
}
