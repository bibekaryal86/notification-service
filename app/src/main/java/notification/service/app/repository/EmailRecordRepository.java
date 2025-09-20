package notification.service.app.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import notification.service.app.model.EmailRecordEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailRecordRepository {
  private static final Logger log = LoggerFactory.getLogger(EmailRecordRepository.class);

  private final Connection connection;

  public EmailRecordRepository(
      final String dbHost, final String dbName, final String dbUsername, final String dbPassword)
      throws SQLException {
    final String dbUrl = String.format("jdbc:postgresql://%s:5432/%s", dbHost, dbName);
    this.connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
  }

  public void save(final EmailRecordEntity emailRecordEntity) {
    log.info(
        "[{}] Save Email Record...To={}, Subject={}, Error={}",
        emailRecordEntity.getId(),
        emailRecordEntity.getEmailTo(),
        emailRecordEntity.getSubject(),
        emailRecordEntity.getErrorMessage());
    final String sql =
        """
            INSERT INTO email_records
            (id, subject, has_html_body, has_text_body, has_attachments,
             email_from, email_to, email_cc, email_bcc, received_at, sent_at, error_message)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    try (final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, emailRecordEntity.getId());
      preparedStatement.setString(2, emailRecordEntity.getSubject());
      preparedStatement.setBoolean(3, emailRecordEntity.isHasHtmlBody());
      preparedStatement.setBoolean(4, emailRecordEntity.isHasTextBody());
      preparedStatement.setBoolean(5, emailRecordEntity.isHasAttachments());
      preparedStatement.setString(6, emailRecordEntity.getEmailFrom());
      preparedStatement.setString(7, emailRecordEntity.getEmailTo());
      preparedStatement.setString(8, emailRecordEntity.getEmailCc());
      preparedStatement.setString(9, emailRecordEntity.getEmailBcc());
      preparedStatement.setTimestamp(
          10,
          emailRecordEntity.getReceivedAt() == null
              ? null
              : Timestamp.valueOf(emailRecordEntity.getReceivedAt()));
      preparedStatement.setTimestamp(
          11,
          emailRecordEntity.getSentAt() == null
              ? null
              : Timestamp.valueOf(emailRecordEntity.getSentAt()));
      preparedStatement.setString(12, emailRecordEntity.getErrorMessage());
      preparedStatement.executeUpdate();
    } catch (SQLException ex) {
      log.error("[{}] Failed to save email record...", emailRecordEntity.getId(), ex);
    }
  }

  public void update(final String id, final LocalDateTime sentAt, final String errorMessage) {
    log.info("[{}] Update Email Record...SentAt={}, ErrorMessage={}", id, sentAt, errorMessage);
    final String sql =
        """
            UPDATE email_records
            SET sent_at = ?, error_message = ?
            WHERE id = ?
            """;

    try (final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setTimestamp(1, sentAt == null ? null : Timestamp.valueOf(sentAt));
      preparedStatement.setString(2, errorMessage);
      preparedStatement.setString(3, id);
      final int updatedRowsCount = preparedStatement.executeUpdate();
      log.info("[{}] Email Records Updated...UpdatedRowsCount={}", id, updatedRowsCount);
    } catch (SQLException ex) {
      log.error("[{}] Failed to update email record...", id, ex);
    }
  }

  public EmailRecordEntity find(final String id, final String currentRequestId) {
    final String sql = "SELECT * FROM email_records WHERE id = ?";

    try (final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, id);
      final ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        return new EmailRecordEntity(
            rs.getString("id"),
            rs.getString("subject"),
            rs.getBoolean("has_html_body"),
            rs.getBoolean("has_text_body"),
            rs.getBoolean("has_attachments"),
            rs.getString("email_from"),
            rs.getString("email_to"),
            rs.getString("email_cc"),
            rs.getString("email_bcc"),
            rs.getTimestamp("received_at").toLocalDateTime(),
            rs.getTimestamp("sent_at") == null
                ? null
                : rs.getTimestamp("sent_at").toLocalDateTime(),
            rs.getString("error_message"));
      }
    } catch (SQLException e) {
      log.error("[{}] Failed to find email record for Id=[{}]", currentRequestId, id);
    }
    return null;
  }

  public void close() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException ex) {
      log.error("Error closing database connection...", ex);
    }
  }
}
