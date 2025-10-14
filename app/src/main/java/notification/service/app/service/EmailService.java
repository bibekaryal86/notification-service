package notification.service.app.service;

import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;
import notification.service.app.model.EmailRequest;
import notification.service.app.model.EmailRequestAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {
  private static final Logger log = LoggerFactory.getLogger(EmailService.class);

  private final Session mailSession;
  private final String smtpEmail;

  public EmailService(
      final String smtpHost,
      final int smtpPort,
      final String smtpEmail,
      final String smtpPassword) {
    this.smtpEmail = smtpEmail;

    Properties properties = new Properties();
    properties.put("mail.smtp.host", smtpHost);
    properties.put("mail.smtp.port", smtpPort);
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");

    this.mailSession =
        Session.getInstance(
            properties,
            new jakarta.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpEmail, smtpPassword);
              }
            });
  }

  public void sendEmail(final EmailRequest emailRequest) throws MessagingException {
    final MimeMessage message = new MimeMessage(mailSession);

    // Set From address
    if (!CommonUtilities.isEmpty(this.smtpEmail)) {
      if (!CommonUtilities.isEmpty(emailRequest.getEmailFromName())) {
        try {
          message.setFrom(
              new InternetAddress(
                  this.smtpEmail, emailRequest.getEmailFromName(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException ignored) {
          message.setFrom(new InternetAddress(this.smtpEmail));
        }
      } else {
        message.setFrom(new InternetAddress(this.smtpEmail));
      }
    }

    // Set recipients
    for (final String emailTo : emailRequest.getEmailRequestRecipients().getEmailTo()) {
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
    }

    if (!CommonUtilities.isEmpty(emailRequest.getEmailRequestRecipients().getEmailCc())) {
      for (final String emailCc : emailRequest.getEmailRequestRecipients().getEmailCc()) {
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(emailCc));
      }
    }

    if (!CommonUtilities.isEmpty(emailRequest.getEmailRequestRecipients().getEmailBcc())) {
      for (final String emailBcc : emailRequest.getEmailRequestRecipients().getEmailBcc()) {
        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailBcc));
      }
    }

    // Set subject
    message.setSubject(emailRequest.getSubject());

    // Create multipart message
    final Multipart multipart = new MimeMultipart();

    // Text body
    if (!CommonUtilities.isEmpty(emailRequest.getTextBody())) {
      final MimeBodyPart textPart = new MimeBodyPart();
      textPart.setText(emailRequest.getTextBody());
      multipart.addBodyPart(textPart);
    }

    // HTML body
    if (!CommonUtilities.isEmpty(emailRequest.getHtmlBody())) {
      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(emailRequest.getHtmlBody(), "text/html; charset=utf-8");
      multipart.addBodyPart(htmlPart);
    }

    // Attachments
    if (!CommonUtilities.isEmpty(emailRequest.getEmailRequestAttachments())) {
      for (EmailRequestAttachment attachment : emailRequest.getEmailRequestAttachments()) {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        byte[] content = Base64.getDecoder().decode(attachment.getContent());

        attachmentPart.setFileName(attachment.getFilename());
        attachmentPart.setContent(content, attachment.getMimeType());
        attachmentPart.setDisposition(Part.ATTACHMENT);

        // For text files, handle differently
        if (attachment.getMimeType().startsWith("text/")) {
          String textContent = new String(content, StandardCharsets.UTF_8);
          attachmentPart.setText(textContent);
          attachmentPart.setFileName(attachment.getFilename());
        }
        multipart.addBodyPart(attachmentPart);
      }
    }

    message.setContent(multipart);

    // Send email
    Transport.send(message);
  }
}
