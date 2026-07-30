package net.patrykdobrowolski.camunda_example.mocks.flights;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlightReservationsRepository extends JpaRepository<FlightReservation, UUID> {
}
