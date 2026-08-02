package net.patrykdobrowolski.camunda_example.mocks.hotels;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "hotel_reservations")
public class HotelReservation {

    @Id
    private UUID id;

    @NotEmpty
    private List<@NotNull UUID> roomsIds;

    @NotNull
    private LocalDate dateFrom;

    @NotNull
    private LocalDate dateTo;

    @NotNull
    private Integer guestsCount;

    @Enumerated(EnumType.STRING)
    private HotelReservationStatus status;

    private Instant createdAt;
}
