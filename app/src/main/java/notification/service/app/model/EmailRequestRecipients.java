package notification.service.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class EmailRequestRecipients implements Serializable {
  private final List<String> emailTo;
  private final List<String> emailCc;
  private final List<String> emailBcc;

  @JsonCreator
  public EmailRequestRecipients(
      @JsonProperty("to") List<String> emailTo,
      @JsonProperty("cc") List<String> emailCc,
      @JsonProperty("bcc") List<String> emailBcc) {
    this.emailTo = emailTo;
    this.emailCc = emailCc;
    this.emailBcc = emailBcc;
  }

  public List<String> getEmailTo() {
    return emailTo;
  }

  public List<String> getEmailCc() {
    return emailCc;
  }

  public List<String> getEmailBcc() {
    return emailBcc;
  }

  @Override
  public String toString() {
    return "EmailRequestRecipients{"
        + "emailTo="
        + emailTo.size()
        + ", emailCc="
        + emailCc.size()
        + ", emailBcc="
        + emailBcc.size()
        + '}';
  }
}
