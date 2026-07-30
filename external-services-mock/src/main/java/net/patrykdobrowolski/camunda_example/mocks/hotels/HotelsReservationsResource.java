package net.patrykdobrowolski.camunda_example.mocks.hotels;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/hotels/reservations")
@RequiredArgsConstructor
@Transactional
public class HotelsReservationsResource {

    private final HotelReservationsRepository hotelReservationsRepository;
    private final Clock clock;

    @PostMapping
    public HotelReservation addReservation(@Valid @RequestBody HotelReservation reservation) {
        reservation.setId(UUID.randomUUID());
        reservation.setStatus(HotelReservationStatus.PENDING);
        reservation.setCreatedAt(Instant.now(clock));
        return hotelReservationsRepository.save(reservation);
    }

    @PutMapping("{reservationId}/confirmation")
    public HotelReservation confirmReservation(@PathVariable UUID reservationId) throws HotelReservationNotFoundException, HotelReservationInvalidStatusException {
        HotelReservation found = hotelReservationsRepository.findById(reservationId).orElseThrow(() -> new HotelReservationNotFoundException(reservationId));
        expectStatusPending(found);
        found.setStatus(HotelReservationStatus.CONFIRMED);
        return hotelReservationsRepository.save(found);
    }

    @DeleteMapping("{reservationId}")
    public HotelReservation cancelReservation(@PathVariable UUID reservationId) throws HotelReservationNotFoundException {
        HotelReservation found = hotelReservationsRepository.findById(reservationId).orElseThrow(() -> new HotelReservationNotFoundException(reservationId));
        found.setStatus(HotelReservationStatus.CANCELLED);
        return hotelReservationsRepository.save(found);
    }

    private static void expectStatusPending(HotelReservation found) throws HotelReservationInvalidStatusException {
        if (found.getStatus() != HotelReservationStatus.PENDING) throw new HotelReservationInvalidStatusException();
    }
}
