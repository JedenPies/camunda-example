package net.patrykdobrowolski.camunda_connector.payment;

import io.camunda.connector.generator.java.annotation.FeelMode;
import io.camunda.connector.generator.java.annotation.TemplateProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Jacksonized
@Builder @Getter
public class ProcessPaymentOutput {

    @TemplateProperty(
            type = TemplateProperty.PropertyType.String,
            feel = FeelMode.optional,
            description = "Correlation key")
    private UUID correlationKey;
//    @TemplateProperty(
//            type = TemplateProperty.PropertyType.String,
//            feel = FeelMode.optional,
//            description = "Payment status")
    private PaymentStatus paymentStatus;
}
