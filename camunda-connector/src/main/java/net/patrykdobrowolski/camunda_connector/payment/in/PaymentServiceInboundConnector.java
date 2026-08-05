package net.patrykdobrowolski.camunda_connector.payment.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.CorrelationRequest;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import lombok.extern.java.Log;
import net.patrykdobrowolski.camunda_connector.config.ObjectMapperConfiguration;
import net.patrykdobrowolski.camunda_connector.config.RabbitConfiguration;
import net.patrykdobrowolski.camunda_connector.config.RabbitConnectionManager;
import net.patrykdobrowolski.camunda_connector.payment.dto.PaymentResultEvent;
import net.patrykdobrowolski.camunda_connector.payment.dto.ProcessPaymentOutput;

import java.nio.charset.StandardCharsets;

import static java.util.logging.Level.SEVERE;

@InboundConnector(
        name = "RABBITMQ_PAYMENT_INBOUND_CONNECTOR",
        type = "net.patrykdobrowolski:payment-service-inbound-connector:1")
@ElementTemplate(
        id = "payment-service-inbound-connector", name = "Payment Service Inbound Connector", description = "An inbound connector for payment service",
        icon = "payment-service-connector.svg")
@Log
public class PaymentServiceInboundConnector implements InboundConnectorExecutable<InboundConnectorContext> {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperConfiguration.getObjectMapper();

    private Channel channel;
    private String consumerTag;
    private InboundConnectorContext context;

    @Override
    public void activate(InboundConnectorContext context) throws Exception {
        String resultQueue = RabbitConfiguration.getInstance().getResultQueue();
        this.context = context;
        this.channel = RabbitConnectionManager.getConnection().createChannel();
        this.consumerTag = channel.basicConsume(resultQueue, false, this::handleDelivery, this::handleCancel);
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

    private void handleDelivery(String consumerTag, Delivery delivery) {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        log.info("Received message: " + message);
        try {
            PaymentResultEvent result = OBJECT_MAPPER.readValue(message, PaymentResultEvent.class);
            ProcessPaymentOutput output = ProcessPaymentOutput.builder().correlationKey(result.getCorrelationKey()).paymentStatus(result.getPaymentResult()).build();
            context.correlate(CorrelationRequest.builder().messageId("result." + result.getCorrelationKey()).variables(output).build());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        } catch (Exception e) {
            log.log(SEVERE, "Error while processing message: " + message, e);
        }
    }

    private void handleCancel(String consumerTag) {
        log.warning("Consumption cancelled by broker.");
    }
}
