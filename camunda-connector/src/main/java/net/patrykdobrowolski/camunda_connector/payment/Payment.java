package net.patrykdobrowolski.camunda_connector.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder @Getter
public class Payment {

    private final PaymentMethod paymentMethod;
    private final String paymentMethodDetails;
}
