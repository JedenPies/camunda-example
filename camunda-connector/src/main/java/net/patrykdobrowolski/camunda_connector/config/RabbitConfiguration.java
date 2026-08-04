package net.patrykdobrowolski.camunda_connector.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitConfiguration {

    private final String host = System.getenv().getOrDefault("RABBITMQ_HOST", "localhost");
    private final Integer port = Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_PORT", "5672"));
    private final String username = System.getenv().getOrDefault("RABBITMQ_USERNAME", "guest");
    private final String password = System.getenv().getOrDefault("RABBITMQ_PASSWORD", "guest");
    private final String virtualHost = System.getenv().getOrDefault("RABBITMQ_VIRTUAL_HOST", "/");
    private final String exchange = System.getenv().getOrDefault("RABBITMQ_EXCHANGE", "payments");
    private final String resultQueue = System.getenv().getOrDefault("RABBITMQ_RESULT_QUEUE", "payments-result");

    private static final class InstanceHolder {
        private static final RabbitConfiguration INSTANCE = new RabbitConfiguration();
    }

    public static RabbitConfiguration getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
