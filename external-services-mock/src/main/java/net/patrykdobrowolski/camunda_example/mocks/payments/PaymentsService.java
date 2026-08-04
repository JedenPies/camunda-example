package net.patrykdobrowolski.camunda_example.mocks.payments;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    public static final String MAGIC_FAIL_NUMBER = "666";

    private final PaymentsRepository paymentsRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void prepareAndSendResponse(PaymentRequestCommand command) {
        Payment payment = preparePayment(command);
        paymentsRepository.save(payment);
        PaymentResultEvent resultEvent = prepareResultEvent(payment).withCorrelationKey(command.getCorrelationKey());
        rabbitTemplate.convertAndSend("payments", "result." + command.getCorrelationKey(), resultEvent, (CorrelationData) null); // TODO take care of rabbit/db synchronization
    }

    private Payment preparePayment(PaymentRequestCommand command) {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .method(command.getPaymentMethod())
                .details(command.getPaymentMethodDetails())
                .amount(command.getAmount())
                .build();
        if (command.getPaymentMethodDetails().endsWith(MAGIC_FAIL_NUMBER)) {
            payment.setResult(PaymentResult.FAILED);
        } else {
            payment.setResult(PaymentResult.SUCCEED);
        }
        return payment;
    }

    private PaymentResultEvent prepareResultEvent(Payment payment) {
        return PaymentResultEvent.builder()
                .paymentId(payment.getId())
                .paymentResult(payment.getResult())
                .build();
    }
}
