package notification.service.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class EmailRequest implements Serializable {
  private final String subject;
  private final String htmlBody;
  private final String textBody;
  private final String emailFromName;
  private final EmailRequestRecipients emailRequestRecipients;
  private final List<EmailRequestAttachment> emailRequestAttachments;

  @JsonCreator
  public EmailRequest(
      @JsonProperty("subject") final String subject,
      @JsonProperty("htmlBody") final String htmlBody,
      @JsonProperty("textBody") final String textBody,
      @JsonProperty("emailFromName") final String emailFromName,
      @JsonProperty("recipients") final EmailRequestRecipients emailRequestRecipients,
      @JsonProperty("attachments") final List<EmailRequestAttachment> emailRequestAttachments) {
    this.subject = subject;
    this.htmlBody = htmlBody;
    this.textBody = textBody;
    this.emailFromName = emailFromName;
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

  public String getEmailFromName() {
    return emailFromName;
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
        + ", isHtmlBodyIncluded='"
        + (htmlBody != null && !htmlBody.trim().isEmpty())
        + '\''
        + ", isTextBodyIncluded='"
        + (textBody != null && !textBody.trim().isEmpty())
        + '\''
        + ", emailFromName="
        + emailFromName
        + ", emailRequestRecipients="
        + emailRequestRecipients
        + ", emailRequestAttachments="
        + emailRequestAttachments
        + '}';
  }
}
