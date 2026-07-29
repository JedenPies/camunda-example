package net.patrykdobrowolski.camundaworker.workers;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MapOfferToHotelAndFlightWorker {

    @JobWorker(type = "map_offer_to_hotel_and_flight")
    public HotelAndFlightDto doJob(@Variable(name = "offerId") String offerId) {
        return HotelAndFlightDto.builder()
                .hotelId(UUID.randomUUID().toString())
                .flightId(UUID.randomUUID().toString())
                .build();
    }
}
