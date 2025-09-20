package notification.service.app.util;

import io.netty.util.AttributeKey;
import java.util.List;

public class Constants {

  // PROVIDED AT RUNTIME
  public static final String ENV_SERVER_PORT = "PORT";
  public static final String ENV_SELF_USER = "SELF_USERNAME";
  public static final String ENV_SELF_PWD = "SELF_PASSWORD";
  public static final String ENV_DB_HOST = "DB_HOST";
  public static final String ENV_DB_NAME = "DB_NAME";
  public static final String ENV_DB_USER = "DB_USERNAME";
  public static final String ENV_DB_PWD = "DB_PASSWORD";
  public static final String ENV_SMTP_HOST = "SMTP_HOST";
  public static final String ENV_SMTP_PORT = "SMTP_PORT";
  public static final String ENV_SMTP_EMAIL = "SMTP_EMAIL";
  public static final String ENV_SMTP_PWD = "SMTP_PASSWORD";

  public static final List<String> ENV_KEY_NAMES =
      List.of(
          ENV_SERVER_PORT,
          ENV_SELF_USER,
          ENV_SELF_PWD,
          ENV_DB_HOST,
          ENV_DB_NAME,
          ENV_DB_USER,
          ENV_DB_PWD,
          ENV_SMTP_HOST,
          ENV_SMTP_PORT,
          ENV_SMTP_EMAIL,
          ENV_SMTP_PWD);

  // NETTY SERVER
  public static final String ENV_PORT_DEFAULT = "8888";
  public static final int BOSS_GROUP_THREADS = 1;
  public static final int WORKER_GROUP_THREADS = Runtime.getRuntime().availableProcessors() * 2;
  public static final int CONNECT_TIMEOUT_MILLIS = 10000; // 10 seconds
  public static final int MAX_CONTENT_LENGTH = 1048576 * 2; // 2 MB
  public static final int SSO_BACKLOG = 128;
  public static final String CONTENT_LENGTH_DEFAULT = "0";
  public static final AttributeKey<String> REQUEST_ID = AttributeKey.valueOf("REQUEST_ID");
  public static final AttributeKey<String> REQUESTED_ID = AttributeKey.valueOf("REQUESTED_ID");
  public static final String SIMPLE_JSON_RESPONSE = "{\"%s\": \"%s\"}";

  // ENDPOINTS
  public static final String THIS_APP_NAME = "notificationsvc";
  private static final String CONTEXT_PATH = THIS_APP_NAME;
  public static final String APP_TESTS_CONTROLLER = "/" + CONTEXT_PATH + "/tests";
  public static final String APP_TESTS_PING = APP_TESTS_CONTROLLER + "/ping";
  public static final String EMAIL_API_CONTROLLER = "/" + CONTEXT_PATH + "/api/v1/email";
  public static final String SEND_EMAIL = EMAIL_API_CONTROLLER + "/send";
  public static final String FIND_EMAIL = EMAIL_API_CONTROLLER + "/find";
}
