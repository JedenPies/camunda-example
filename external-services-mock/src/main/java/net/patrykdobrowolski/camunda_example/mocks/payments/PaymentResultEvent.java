package net.patrykdobrowolski.camunda_example.mocks.payments;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.UUID;

@Data
@Builder
public class PaymentResultEvent {

    @With
    private final UUID correlationKey;
    private final UUID paymentId;
    private final PaymentResult paymentResult;
}
