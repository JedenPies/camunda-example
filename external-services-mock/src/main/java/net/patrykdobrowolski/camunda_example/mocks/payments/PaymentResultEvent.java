package net.patrykdobrowolski.camunda_example.mocks.payments;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Jacksonized
@Builder @Getter @RequiredArgsConstructor
public class PaymentResultEvent {

    @With
    private final UUID correlationKey;
    private final UUID paymentId;
    private final PaymentResult paymentResult;
}
