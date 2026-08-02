package net.patrykdobrowolski.camundaworker.workers.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Jacksonized
@Builder @Getter
@NoArgsConstructor @AllArgsConstructor
public class Offer {

    private UUID id;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private UUID flightId;
    private List<UUID> hotelRoomsIds;
    private Integer participantCount;
    private BigDecimal price;
}
