package net.patrykdobrowolski.camunda_connector.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.UUID;

@Jacksonized
@Builder @AllArgsConstructor
@Getter
public class PaymentDto {

    private UUID id;
    private PaymentMethod method;
    private String details;
    private BigDecimal amount;
    private PaymentStatus status;
}
