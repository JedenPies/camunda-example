package net.patrykdobrowolski.camunda_example.mocks.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelPayment(@PathVariable UUID paymentId) {
        paymentsService.cancelPayment(paymentId);
    }
}
