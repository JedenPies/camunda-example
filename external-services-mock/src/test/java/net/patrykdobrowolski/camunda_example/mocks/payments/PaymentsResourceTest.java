package net.patrykdobrowolski.camunda_example.mocks.payments;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentsResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test_shouldPaymentHaveStatusPendingJustAfterItsCreated() throws Exception {
        UUID paymentId = UUID.randomUUID();
        PaymentRequestCommand request = PaymentRequestCommand.builder()
                .correlationKey(paymentId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentMethodDetails("666")
                .amount(BigDecimal.valueOf(100.03))
                .build();
        postNewPayment(request).andExpect(status().isOk()).andReturn();
        Awaitility.await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Payment payment = paymentsRepository.findById(paymentId).orElseThrow();
            Assertions.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        });
    }

    @Test
    public void test_shouldPaymentHaveStatusPendingCancelledBeforeResultButAfterItsCancelled() throws Exception {
        UUID paymentId = UUID.randomUUID();
        PaymentRequestCommand request = PaymentRequestCommand.builder()
                .correlationKey(paymentId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentMethodDetails("666")
                .amount(BigDecimal.valueOf(100.03))
                .build();
        postNewPayment(request).andExpect(status().isOk()).andReturn();
        cancelPayment(paymentId).andReturn();
        Payment payment = paymentsRepository.findById(paymentId).orElseThrow();
        Assertions.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING_CANCELLED);
    }

    @Test
    public void test_shouldFailPaymentWhenResultWithMagicCardNumber() throws Exception {
        UUID paymentId = UUID.randomUUID();
        PaymentRequestCommand request = PaymentRequestCommand.builder()
                .correlationKey(paymentId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentMethodDetails("666")
                .amount(BigDecimal.valueOf(100))
                .build();
        postNewPayment(request).andExpect(status().isOk()).andReturn();
        PaymentResultEvent result = expectRabbitTemplateCall(paymentId, 3);
        Payment paymentInDb = paymentsRepository.findById(paymentId).orElseThrow();
        Assertions.assertThat(paymentInDb.getStatus()).isEqualTo(PaymentStatus.FAILED);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentId()).isEqualTo(paymentId);
        Assertions.assertThat(result.getPaymentResult()).isEqualTo(PaymentResult.FAILED);
    }

    @Test
    public void test_shouldSuccessPayment() throws Exception {
        UUID paymentId = UUID.randomUUID();
        PaymentRequestCommand request = PaymentRequestCommand.builder()
                .correlationKey(paymentId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentMethodDetails("6661")
                .amount(BigDecimal.valueOf(100))
                .build();
        postNewPayment(request).andExpect(status().isOk()).andReturn();
        PaymentResultEvent result = expectRabbitTemplateCall(paymentId, 3);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentId()).isEqualTo(paymentId);
        Assertions.assertThat(result.getPaymentResult()).isEqualTo(PaymentResult.SUCCEED);
    }

    @Test
    public void test_shouldCancelPaymentWhenCancellationHappenedBeforeSuccess() throws Exception {
        UUID paymentId = UUID.randomUUID();
        PaymentRequestCommand request = PaymentRequestCommand.builder()
                .correlationKey(paymentId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentMethodDetails("6661")
                .amount(BigDecimal.valueOf(100.10))
                .build();
        postNewPayment(request).andExpect(status().isOk()).andReturn();
        cancelPayment(paymentId).andExpect(status().isOk()).andReturn();
        PaymentResultEvent result = expectRabbitTemplateCall(paymentId, 15);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentId()).isEqualTo(paymentId);
        Assertions.assertThat(result.getPaymentResult()).isEqualTo(PaymentResult.CANCELLED);
    }

    @Test
    public void test_shouldRefundPaymentAfterSuccess() throws Exception {
        UUID paymentId = UUID.randomUUID();
        PaymentRequestCommand request = PaymentRequestCommand.builder()
                .correlationKey(paymentId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentMethodDetails("6661")
                .amount(BigDecimal.valueOf(100))
                .build();
        postNewPayment(request).andExpect(status().isOk()).andReturn();
        Awaitility.await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Payment payment = paymentsRepository.findById(paymentId).orElseThrow();
            Assertions.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCEED);
        });
        cancelPayment(paymentId);
        Awaitility.await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Payment payment = paymentsRepository.findById(paymentId).orElseThrow();
            Assertions.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        });
    }

    @Test
    public void test_shouldCancelPaymentAfterFail() throws Exception {
        UUID paymentId = UUID.randomUUID();
        PaymentRequestCommand request = PaymentRequestCommand.builder()
                .correlationKey(paymentId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentMethodDetails("666")
                .amount(BigDecimal.valueOf(100))
                .build();
        postNewPayment(request).andExpect(status().isOk()).andReturn();
        Awaitility.await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Payment payment = paymentsRepository.findById(paymentId).orElseThrow();
            Assertions.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        });
        cancelPayment(paymentId);
        Awaitility.await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Payment payment = paymentsRepository.findById(paymentId).orElseThrow();
            Assertions.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        });
    }

    private PaymentResultEvent expectRabbitTemplateCall(UUID paymentId, long timeout) {
        ArgumentCaptor<PaymentResultEvent> resultCaptor = ArgumentCaptor.forClass(PaymentResultEvent.class);
        Awaitility.await().atMost(timeout, TimeUnit.SECONDS).untilAsserted(() -> {
            Mockito.verify(rabbitTemplate).convertAndSend(
                eq("payments"),
                eq("result." + paymentId),
                resultCaptor.capture());

        });
        return resultCaptor.getValue();
    }

    private @NonNull ResultActions postNewPayment(PaymentRequestCommand request) throws Exception {
        return mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private @NonNull ResultActions cancelPayment(UUID paymentId) throws Exception {
        return mockMvc.perform(delete("/api/payments/" + paymentId)
                .contentType(MediaType.APPLICATION_JSON));
    }

}
