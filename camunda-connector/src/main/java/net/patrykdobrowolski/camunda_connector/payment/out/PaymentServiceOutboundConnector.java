package net.patrykdobrowolski.camunda_connector.payment.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.camunda.connector.api.annotation.Operation;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.annotation.Variable;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.outbound.OutboundConnectorProvider;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import lombok.extern.java.Log;
import net.patrykdobrowolski.camunda_connector.config.ExternalServicesConfiguration;
import net.patrykdobrowolski.camunda_connector.config.ObjectMapperConfiguration;
import net.patrykdobrowolski.camunda_connector.config.RabbitConfiguration;
import net.patrykdobrowolski.camunda_connector.config.RabbitConnectionManager;
import net.patrykdobrowolski.camunda_connector.payment.dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@OutboundConnector(
        name = "PAYMENT_SERVICE_OUTBOUND_CONNECTOR",
        type = "net.patrykdobrowolski:payment-service-outbound-connector:1")
@ElementTemplate(
        id = "payment-service-outbound-connector", name = "Payment Service Outbound Connector", description = "A connector for payment service",
        icon = "payment-service-connector.svg")
@Log
public class PaymentServiceOutboundConnector implements OutboundConnectorProvider {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperConfiguration.getObjectMapper();
    private static final RabbitConfiguration RABBIT_CONFIGURATION = RabbitConfiguration.getInstance();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @Operation(name = "Request Payment", id = "request-payment")
    public String requestPayment(@Variable ProcessPaymentInput input) throws IOException, TimeoutException {

        UUID correlationKey = UUID.randomUUID();
        log.info("Requesting payment for: " + input);
        Connection connection = RabbitConnectionManager.getConnection();
        try (Channel channel = connection.createChannel()) {
            log.info("connected to rabbitmq");
            PaymentRequestCommand paymentRequestCommand = PaymentRequestCommand.builder()
                    .paymentMethod(input.getPaymentMethod())
                    .paymentMethodDetails(input.getPaymentMethodDetails())
                    .amount(input.getAmount())
                    .correlationKey(correlationKey)
                    .build();
            String jsonPayload = OBJECT_MAPPER.writeValueAsString(paymentRequestCommand);
            channel.basicPublish(
                    RABBIT_CONFIGURATION.getExchange(),
                    "request." + correlationKey,
                    null,
                    jsonPayload.getBytes(StandardCharsets.UTF_8)
            );
            log.info("Payment request sent");
        }
        return correlationKey.toString();
    }

    @Operation(name = "Cancel Payment", id = "cancel-payment")
    public ProcessPaymentOutput cancelPayment(@Variable(name = "correlationKey") String correlationKey) throws IOException, InterruptedException {
        HttpRequest request = prepareCancellationRequest(correlationKey);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleCancellationResponse(response);
    }

    private static HttpRequest prepareCancellationRequest(String paymentId) {

        return HttpRequest.newBuilder()
                .uri(URI.create(ExternalServicesConfiguration.getInstance().getPaymentServiceUrl() + "/" + paymentId))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
    }

    private static ProcessPaymentOutput handleCancellationResponse(HttpResponse<String> response) throws JsonProcessingException {
        if (response.statusCode() == 200) {
            PaymentDto responseObject = OBJECT_MAPPER.readValue(response.body(), PaymentDto.class);
            if (responseObject.getStatus() == PaymentStatus.CANCELLED || responseObject.getStatus() == PaymentStatus.REFUNDED) {
                return ProcessPaymentOutput.builder()
                        .correlationKey(responseObject.getId())
                        .paymentStatus(responseObject.getStatus())
                        .build();
            }
        }
        throw new ConnectorException("PAYMENT_CANCEL_FAILED", "canceling reservation failed");
    }
}
