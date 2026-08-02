package net.patrykdobrowolski.camundaworker.workers.hotel;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "hotel-reservation-service", url = "${external_service_host_port}/api/hotels/reservations")
public interface HotelReservationService {

    @PostMapping
    HotelReservation addReservation(@RequestBody HotelReservation reservation);

    @PutMapping("{reservationId}/confirmation")
    HotelReservation confirmReservation(@PathVariable UUID reservationId);

    @DeleteMapping("{reservationId}")
    HotelReservation cancelReservation(@PathVariable UUID reservationId);
}
