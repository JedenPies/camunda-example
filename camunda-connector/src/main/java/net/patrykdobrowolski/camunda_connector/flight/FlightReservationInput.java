package net.patrykdobrowolski.camunda_connector.flight;

import io.camunda.connector.generator.java.annotation.FeelMode;
import io.camunda.connector.generator.java.annotation.TemplateProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
public class FlightReservationInput {

    private final String flightId;

    @TemplateProperty(
            type = TemplateProperty.PropertyType.String,
            feel = FeelMode.optional,
            description = "Number of seats to reserve")
    private final Integer seats;
}
