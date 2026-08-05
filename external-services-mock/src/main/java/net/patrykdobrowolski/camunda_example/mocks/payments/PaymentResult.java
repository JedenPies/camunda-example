package net.patrykdobrowolski.camunda_example.mocks.payments;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PaymentResult {


    PENDING(Set.of(PaymentStatus.PENDING)),
    SUCCEED(Set.of(PaymentStatus.SUCCEED)),
    FAILED(Set.of(PaymentStatus.FAILED)),
    CANCELLED(Set.of(PaymentStatus.CANCELLED, PaymentStatus.REFUNDED, PaymentStatus.PENDING_CANCELLED));

    private final Set<PaymentStatus> status;

    public static PaymentResult from(PaymentStatus paymentStatus) {
        return Stream.of(PaymentResult.values())
                .filter(pr -> pr.status.contains(paymentStatus))
                .findFirst().orElseThrow();
    }
}
