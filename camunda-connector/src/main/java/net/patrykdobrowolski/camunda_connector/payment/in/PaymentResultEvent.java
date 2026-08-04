package net.patrykdobrowolski.camunda_connector.payment.in;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Jacksonized
@Builder @Getter
public class PaymentResultEvent {

    private UUID correlationKey;
    private UUID paymentId;
    private PaymentStatus paymentResult;
}
