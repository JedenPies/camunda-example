package net.patrykdobrowolski.camunda_example.mocks.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsResource {

    private final PaymentsService paymentsService;
    private final PaymentsCommandsListener paymentsCommandsListener;

    @PostMapping
    public void createNewPayment(@RequestBody PaymentRequestCommand command) {
        paymentsCommandsListener.onPaymentRequestEvent(command);
    }

    @DeleteMapping("/{paymentId}")
    public Payment cancelPayment(@PathVariable UUID paymentId) {
        return paymentsService.cancelPayment(paymentId);
    }
}
