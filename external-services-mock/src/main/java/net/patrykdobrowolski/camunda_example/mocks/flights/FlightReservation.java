package net.patrykdobrowolski.camunda_example.mocks.flights;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "flight_reservations")
public class FlightReservation {

    @Id
    private UUID id;

    @NotNull
    private UUID flightId;

    @NotNull @Min(1) @Max(10)
    private Integer seats;

    private FlightReservationStatus status;
}
