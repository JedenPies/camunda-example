package net.patrykdobrowolski.camunda_example.mocks.payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Payment {

    @Id
    @Getter
    private UUID id;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    private String details;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Getter
    private PaymentStatus status = PaymentStatus.PENDING;
    @Version @JsonIgnore
    private Long version;

    public void confirmSuccess() {
        status = switch (status) {
            case PENDING -> PaymentStatus.SUCCEED;
            case PENDING_CANCELLED -> PaymentStatus.CANCELLED;
            default ->  throw new IllegalStateException("Unexpected status: " + status);
        };
    }

    public void makeFailed() {
        status = switch (status) {
            case PENDING -> PaymentStatus.FAILED;
            case PENDING_CANCELLED -> PaymentStatus.CANCELLED;
            default ->  throw new IllegalStateException("Unexpected status: " + status);
        };
    }

    public void cancel() {
        status = switch (status) {
            case SUCCEED -> PaymentStatus.REFUNDED;
            case PENDING, PENDING_CANCELLED -> PaymentStatus.PENDING_CANCELLED;
            case FAILED -> PaymentStatus.CANCELLED;
            default ->  throw new IllegalStateException("Unexpected status: " + status);
        };
    }
}
