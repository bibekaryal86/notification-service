package notification.service.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class EmailRequest implements Serializable {
  private final String subject;
  private final String htmlBody;
  private final String textBody;
  private final EmailRequestRecipients emailRequestRecipients;
  private final List<EmailRequestAttachment> emailRequestAttachments;

  @JsonCreator
  public EmailRequest(
      @JsonProperty("subject") final String subject,
      @JsonProperty("htmlBody") final String htmlBody,
      @JsonProperty("textBody") final String textBody,
      @JsonProperty("recipients") final EmailRequestRecipients emailRequestRecipients,
      @JsonProperty("attachments") final List<EmailRequestAttachment> emailRequestAttachments) {
    this.subject = subject;
    this.htmlBody = htmlBody;
    this.textBody = textBody;
    this.emailRequestRecipients = emailRequestRecipients;
    this.emailRequestAttachments = emailRequestAttachments;
  }

  public String getSubject() {
    return subject;
  }

  public String getHtmlBody() {
    return htmlBody;
  }

  public String getTextBody() {
    return textBody;
  }

  public EmailRequestRecipients getEmailRequestRecipients() {
    return emailRequestRecipients;
  }

  public List<EmailRequestAttachment> getEmailRequestAttachments() {
    return emailRequestAttachments;
  }

  @Override
  public String toString() {
    return "EmailRequest{"
        + "subject='"
        + subject
        + '\''
        + ", htmlBody='"
        + (htmlBody == null || htmlBody.trim().isEmpty())
        + '\''
        + ", textBody='"
        + (textBody == null || textBody.trim().isEmpty())
        + '\''
        + ", emailRequestRecipients="
        + emailRequestRecipients
        + ", emailRequestAttachments="
        + emailRequestAttachments
        + '}';
  }
}
