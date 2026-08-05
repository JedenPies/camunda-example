package net.patrykdobrowolski.camunda_connector.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExternalServicesConfiguration {

    private final String flightServiceUrl = System.getenv().getOrDefault("FLIGHT_SERVICE_URL", "http://localhost:8090/api/flights");
    private final String paymentServiceUrl = System.getenv().getOrDefault("PAYMENT_SERVICE_URL", "http://localhost:8090/api/payments");

    private static final class InstanceHolder {
        private static final ExternalServicesConfiguration INSTANCE = new ExternalServicesConfiguration();
    }

    public static ExternalServicesConfiguration getInstance() {
        return ExternalServicesConfiguration.InstanceHolder.INSTANCE;
    }
}
