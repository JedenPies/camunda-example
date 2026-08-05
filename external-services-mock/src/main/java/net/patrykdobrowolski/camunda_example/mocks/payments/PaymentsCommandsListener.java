package net.patrykdobrowolski.camunda_example.mocks.payments;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsCommandsListener {

    private final PaymentsService paymentsService;

    @RabbitListener(queues = "payment-requests")
    public void onPaymentRequestEvent(@Valid PaymentRequestCommand event) {
        paymentsService.beginPayment(event);
        int delayInSeconds = event.getAmount().remainder(BigDecimal.ONE).movePointRight(2).abs().intValue();
        CompletableFuture.runAsync(() -> this.handleEvent(event), CompletableFuture.delayedExecutor(delayInSeconds, TimeUnit.SECONDS)).exceptionally(throwable -> {
            log.error("Error occurred while processing payment request: {}", throwable.getMessage());
            // TODO to be handled in the future
            return null;
        });
    }

    private void handleEvent(PaymentRequestCommand event) {
        if (event.getPaymentMethodDetails().endsWith("666")) {
            paymentsService.confirmPaymentFailed(event.getCorrelationKey());
        } else {
            paymentsService.confirmPaymentSucceed(event.getCorrelationKey());
        }
    }
}
