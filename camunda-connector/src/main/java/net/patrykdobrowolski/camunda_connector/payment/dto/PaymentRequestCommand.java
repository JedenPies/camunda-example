package net.patrykdobrowolski.camunda_connector.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.UUID;

@Jacksonized
@Builder @Getter
public class PaymentRequestCommand {

    private UUID correlationKey;
    private PaymentMethod paymentMethod;
    private String paymentMethodDetails;
    private BigDecimal amount;
}
