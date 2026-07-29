package net.patrykdobrowolski.example_camunda_connector;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder @Getter
public class FlightResponseDto {

    private final String reservationNumber;
    private final ReservationStatus status;
}
