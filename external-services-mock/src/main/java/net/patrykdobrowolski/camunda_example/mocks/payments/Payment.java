package net.patrykdobrowolski.camunda_example.mocks.payments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "payments")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Payment {

    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    private String details;
    @Enumerated(EnumType.STRING)
    private PaymentResult result;
    private BigDecimal amount;
}
