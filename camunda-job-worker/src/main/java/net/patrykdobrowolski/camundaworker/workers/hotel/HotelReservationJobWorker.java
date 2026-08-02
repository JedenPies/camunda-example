package net.patrykdobrowolski.camundaworker.workers.hotel;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.exception.BpmnError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class HotelReservationJobWorker {

    private final HotelReservationService hotelReservationService;

    @JobWorker(type = "reserve-hotel")
    public Map<String, Object> reserveHotel(@Variable(name = "offer") Offer offer) {
        HotelReservation request = HotelReservation.builder()
                .dateFrom(offer.getDateFrom())
                .dateTo(offer.getDateTo())
                .guestsCount(offer.getParticipantCount())
                .roomsIds(offer.getHotelRoomsIds())
                .build();
        HotelReservation result = hotelReservationService.addReservation(request);
        if (result.getStatus() != HotelReservationStatus.PENDING) {
            throw new BpmnError("RESERVATION_FAILED", "Hotel reservation failed");
        }
        return Map.of("hotelReservation", result);
    }

    @JobWorker(type = "cancel-hotel-reservation")
    public Map<String, Object> cancelReservation(@Variable(name = "hotelReservation") HotelReservation hotelReservation) {
        HotelReservation result = hotelReservationService.cancelReservation(hotelReservation.getId());
        if (result.getStatus() != HotelReservationStatus.CANCELLED) {
            throw new BpmnError("RESERVATION_CANCELLATION_FAILED", "Hotel reservation cancellation failed");
        }
        return Map.of("hotelReservation", result);
    }
}
