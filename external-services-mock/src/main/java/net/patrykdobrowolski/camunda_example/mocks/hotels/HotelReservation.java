package net.patrykdobrowolski.camunda_example.mocks.hotels;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String userEmail;

    @NotNull
    private LocalDate dateFrom;

    @NotNull
    private LocalDate dateTo;

    @NotNull
    private Integer guestsCount;

    private HotelReservationStatus status;

    private Instant createdAt;
}
