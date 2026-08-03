package net.patrykdobrowolski.camunda_connector.payment;

import io.camunda.connector.generator.java.annotation.FeelMode;
import io.camunda.connector.generator.java.annotation.TemplateProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Jacksonized
@Builder @Getter
public class Offer {

    @TemplateProperty(
            type = TemplateProperty.PropertyType.String,
            feel = FeelMode.optional,
            description = "Number of seats to reserve")
    private BigDecimal price;
}
