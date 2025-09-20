package notification.service.app.model;

public record ServerConfig(
    int port,
    int bossThreads,
    int workerThreads,
    int maxContentLength,
    int connectTimeoutMillis,
    int soBacklog) {}
