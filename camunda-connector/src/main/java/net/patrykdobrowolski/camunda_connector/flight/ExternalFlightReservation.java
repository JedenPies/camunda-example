package net.patrykdobrowolski.camunda_connector.flight;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Jacksonized @Builder
@Getter
public class ExternalFlightReservation {

    private UUID id;
    private UUID flightId;
    private Integer seats;
    private ExternalFlightReservationStatus status;
}
