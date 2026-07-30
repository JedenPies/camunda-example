package net.patrykdobrowolski.camunda_example.mocks.flights;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/flights/reservations")
@RequiredArgsConstructor
public class FlightReservationsResource {

    private final FlightReservationsRepository flightReservationsRepository;

    @PostMapping
    public FlightReservation createFlightReservation(@Valid @RequestBody FlightReservation flightReservation) {
        flightReservation.setId(UUID.randomUUID());
        flightReservation.setStatus(FlightReservationStatus.PENDING);
        return flightReservationsRepository.save(flightReservation);
    }

    @PutMapping("/{id}/confirmation")
    public FlightReservation confirmFlightReservation(@PathVariable UUID id) throws FlightReservationNotFoundException, InvalidFlightReservationStatusException {
        FlightReservation flightReservation = flightReservationsRepository.findById(id).orElseThrow(FlightReservationNotFoundException::new);
        expectStatusPending(flightReservation);
        flightReservation.setStatus(FlightReservationStatus.CONFIRMED);
        return flightReservationsRepository.save(flightReservation);
    }

    @DeleteMapping("/{id}")
    public FlightReservation cancelFlightReservation(@PathVariable UUID id) throws FlightReservationNotFoundException {
        FlightReservation flightReservation = flightReservationsRepository.findById(id).orElseThrow(FlightReservationNotFoundException::new);
        flightReservation.setStatus(FlightReservationStatus.CANCELLED);
        return flightReservationsRepository.save(flightReservation);
    }

    private void expectStatusPending(FlightReservation flightReservation) throws InvalidFlightReservationStatusException {
        if (flightReservation.getStatus() != FlightReservationStatus.PENDING) throw new InvalidFlightReservationStatusException();
    }
}
