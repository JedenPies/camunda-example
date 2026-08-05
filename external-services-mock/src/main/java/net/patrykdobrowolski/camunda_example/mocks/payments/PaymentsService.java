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

    private final PaymentsRepository paymentsRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void beginPayment(PaymentRequestCommand command) {
        Payment newPayment = prepareNewPayment(command);
        paymentsRepository.save(newPayment);
    }

    @Transactional
    public void confirmPaymentSucceed(UUID correlationKey) {
        Payment found = paymentsRepository.findById(correlationKey).orElseThrow();
        found.confirmSuccess();
        paymentsRepository.save(found);
        PaymentResultEvent event = prepareResultEvent(found);
        rabbitTemplate.convertAndSend("payments", "result." + found.getId(), event);
    }

    @Transactional
    public void confirmPaymentFailed(UUID correlationKey) {
        Payment found = paymentsRepository.findById(correlationKey).orElseThrow();
        found.makeFailed();
        paymentsRepository.save(found);
        PaymentResultEvent event = prepareResultEvent(found);
        rabbitTemplate.convertAndSend("payments", "result." + found.getId(), event);
    }

    @Transactional
    public void cancelPayment(UUID correlationKey) {
        Payment found = paymentsRepository.findById(correlationKey).orElseThrow();
        found.cancel();
        paymentsRepository.save(found);
        PaymentResultEvent event = prepareResultEvent(found);
        rabbitTemplate.convertAndSend("payments", "result." + found.getId(), event, (CorrelationData) null); // TODO take care of rabbit/db synchronization
    }

    private Payment prepareNewPayment(PaymentRequestCommand command) {
        return Payment.builder()
                .id(command.getCorrelationKey())
                .method(command.getPaymentMethod())
                .details(command.getPaymentMethodDetails())
                .amount(command.getAmount())
                .status(PaymentStatus.PENDING)
                .build();
    }

    private PaymentResultEvent prepareResultEvent(Payment payment) {
        return PaymentResultEvent.builder()
                .correlationKey(payment.getId())
                .paymentId(payment.getId())
                .paymentResult(PaymentResult.from(payment.getStatus()))
                .build();
    }
}
