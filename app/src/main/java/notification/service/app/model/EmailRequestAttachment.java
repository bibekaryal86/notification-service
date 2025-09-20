package notification.service.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class EmailRequestAttachment implements Serializable {
  private final String filename;
  private final String content; // Base64 encoded content
  private final String mimeType;

  @JsonCreator
  public EmailRequestAttachment(
      @JsonProperty("fileName") final String filename,
      @JsonProperty("content") final String content,
      @JsonProperty("mimeType") final String mimeType) {
    this.filename = filename;
    this.content = content;
    this.mimeType = mimeType;
  }

  public String getFilename() {
    return filename;
  }

  public String getContent() {
    return content;
  }

  public String getMimeType() {
    return mimeType;
  }

  @Override
  public String toString() {
    return "EmailRequestAttachments{"
        + "filename='"
        + filename
        + '\''
        + ", content='"
        + (content == null)
        + '\''
        + ", mimeType='"
        + mimeType
        + '\''
        + '}';
  }
}
