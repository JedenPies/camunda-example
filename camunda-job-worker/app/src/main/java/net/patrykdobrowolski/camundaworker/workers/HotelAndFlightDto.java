package net.patrykdobrowolski.camundaworker.workers;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HotelAndFlightDto {

    private final String hotelId;
    private final String flightId;
}
