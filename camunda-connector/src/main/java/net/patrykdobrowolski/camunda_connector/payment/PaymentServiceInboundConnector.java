package net.patrykdobrowolski.camunda_connector.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.CorrelationRequest;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import lombok.extern.java.Log;

import java.nio.charset.StandardCharsets;

import static java.util.logging.Level.SEVERE;

@InboundConnector(
        name = "RABBITMQ_PAYMENT_INBOUND",
        type = "net.patrykdobrowolski:payment-service-inbound-connector:1")
@ElementTemplate(
        id = "payment-service-inbound-connector", name = "Payment Service Inbound Connector", description = "An inbound connector for payment service",
        icon = "payment-service-connector.svg")
@Log
public class PaymentServiceInboundConnector implements InboundConnectorExecutable<InboundConnectorContext> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Channel channel;
    private String consumerTag;

    @Override
    public void activate(InboundConnectorContext context) throws Exception {

        channel = RabbitConnectionManager.getConnection().createChannel();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.info("Received message: " + message);
            try {
                PaymentResultEvent result = OBJECT_MAPPER.readValue(message, PaymentResultEvent.class);
                ProcessPaymentOutput output = ProcessPaymentOutput.builder().correlationKey(result.getCorrelationKey()).paymentStatus(result.getPaymentResult()).build();
                context.correlate(CorrelationRequest.builder().messageId("result." + result.getCorrelationKey()).variables(output).build());
            } catch (Exception e) {
                log.log(SEVERE, "Error while processing message: " + message, e);
            }
        };
        String resultQueue = RabbitConfiguration.getInstance().getResultQueue();
        this.consumerTag = channel.basicConsume(resultQueue, true, deliverCallback, consumerTag -> {
            log.warning("Consumption cancelled by broker.");
        });
    }

    @Override
    public void deactivate() throws Exception {
        log.info("Inbound Connector deactivated - closing connections.");
        if (channel != null && channel.isOpen()) {
            if (consumerTag != null) {
                channel.basicCancel(consumerTag);
            }
            channel.close();
        }
    }
}
