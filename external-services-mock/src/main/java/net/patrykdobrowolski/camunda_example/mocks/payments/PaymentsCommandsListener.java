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
    public void onPaymentEvent(@Valid PaymentRequestCommand event) {
        int delayInSeconds = event.getAmount().remainder(BigDecimal.ONE).movePointRight(2).abs().intValue();
        CompletableFuture.runAsync(() -> paymentsService.prepareAndSendResponse(event), CompletableFuture.delayedExecutor(delayInSeconds, TimeUnit.SECONDS)).exceptionally(throwable -> {
            log.error("Error occurred while processing payment request: {}", throwable.getMessage());
            // TODO to be handled in the future
            return null;
        });
    }
}
