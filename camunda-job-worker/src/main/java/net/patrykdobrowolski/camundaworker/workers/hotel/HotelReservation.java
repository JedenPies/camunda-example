package net.patrykdobrowolski.camundaworker.workers.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Jacksonized @Builder
@Getter @NoArgsConstructor @AllArgsConstructor
public class HotelReservation {

    private UUID id;
    private List<UUID> roomsIds;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer guestsCount;
    private HotelReservationStatus status;
    private Instant createdAt;
}
