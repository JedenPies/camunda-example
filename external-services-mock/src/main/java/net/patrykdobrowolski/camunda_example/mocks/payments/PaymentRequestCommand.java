package net.patrykdobrowolski.camunda_example.mocks.payments;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.UUID;

@Jacksonized
@Builder @AllArgsConstructor
@Getter
public class PaymentRequestCommand {

    @NotNull
    private UUID correlationKey;
    @NotNull
    private PaymentMethod paymentMethod;
    @NotNull
    private String paymentMethodDetails; // e.g. credit card number, blik customer id, etc.
    @NotNull @Min(1)
    private BigDecimal amount;

    /*
     {
        "correlationKey":"05aea39e-ab8e-4871-ad6d-eb307d7e606c",
        "paymentMethod":CREDIT_CARD,
        "paymentMethodDetails":"123456",
        "amount":100
     }
     */
}
