package net.patrykdobrowolski.camunda_example.mocks.payments;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestCommand {

    @NotNull
    private UUID correlationKey;
    @NotNull
    private PaymentMethod paymentMethod;
    @NotNull
    private String paymentMethodDetails; // e.g. credit card number, blik customer id, etc.
    @NotNull @Min(1)
    private BigDecimal amount;
}
