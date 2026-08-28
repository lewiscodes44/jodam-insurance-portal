package ke.co.jodam.insurance.repository;

import ke.co.jodam.insurance.entity.Payment;
import ke.co.jodam.insurance.entity.PaymentStatus;
import ke.co.jodam.insurance.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByPolicyId(
            Long policyId
    );

    Optional<Payment>
    findFirstByPolicyIdAndStatusOrderByIdDesc(
            Long policyId,
            PaymentStatus status
    );

    Optional<Payment>
    findFirstByPolicyIdOrderByIdDesc(
            Long policyId
    );

    Optional<Payment>
    findByTransactionReference(
            String transactionReference
    );

    Optional<Payment>
    findByCheckoutRequestId(
            String checkoutRequestId
    );

    Optional<Payment>
    findFirstByCheckoutRequestIdOrderByIdDesc(
            String checkoutRequestId
    );

    List<Payment>
    findByStatus(
            PaymentStatus status
    );

    List<Payment>
    findByPolicy_Customer(
            User customer
    );

    long countByStatus(
            PaymentStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(payment.amount), 0)
            FROM Payment payment
            WHERE payment.status =
            ke.co.jodam.insurance.entity.PaymentStatus.COMPLETED
            """)
    BigDecimal sumCompletedPaymentAmount();
}