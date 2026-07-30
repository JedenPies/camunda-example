package net.patrykdobrowolski.camunda_example.mocks.payments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentsRepository extends JpaRepository<Payment, UUID> {
}
