package net.patrykdobrowolski.example_camunda_connector;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Jacksonized
@Builder @Getter
public class FlightResponseDto {

    private final UUID reservationNumber;
    private final FlightReservationStatus status;
}
