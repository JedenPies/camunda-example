package net.patrykdobrowolski.camunda_example.mocks.hotels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HotelReservationsRepository extends JpaRepository<HotelReservation, UUID> {
}
