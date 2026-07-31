package net.patrykdobrowolski.camunda_example.mocks.offers;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "offers")
public class Offer {

    @Id
    private UUID id;

    @NotNull
    private LocalDate dateFrom;

    @NotNull
    private LocalDate dateTo;

    @NotNull
    private UUID flightId;

    @NotEmpty
    private List<@NotNull UUID> hotelRoomsIds;

    @NotNull
    private Integer participantCount;

    @NotNull
    private BigDecimal price;
}
