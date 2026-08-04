package net.patrykdobrowolski.camunda_connector.payment.out;

import io.camunda.connector.generator.java.annotation.FeelMode;
import io.camunda.connector.generator.java.annotation.TemplateProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Jacksonized
@Builder @Getter
@ToString
public class ProcessPaymentInput {

    private final PaymentMethod paymentMethod;
    private final String paymentMethodDetails;
    @TemplateProperty(
            type = TemplateProperty.PropertyType.String,
            feel = FeelMode.optional,
            description = "Amount to pay")
    private final BigDecimal amount;
}
