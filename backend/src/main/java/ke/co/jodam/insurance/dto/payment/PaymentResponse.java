package ke.co.jodam.insurance.dto.payment;

import ke.co.jodam.insurance.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long policyId;
    private String policyNumber;
    private BigDecimal amount;
    private String phoneNumber;
    private String transactionReference;
    private String checkoutRequestId;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentResponse() {
    }

    public PaymentResponse(
            Long id,
            Long policyId,
            String policyNumber,
            BigDecimal amount,
            String phoneNumber,
            String transactionReference,
            String checkoutRequestId,
            PaymentStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.amount = amount;
        this.phoneNumber = phoneNumber;
        this.transactionReference = transactionReference;
        this.checkoutRequestId = checkoutRequestId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getCheckoutRequestId() {
        return checkoutRequestId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}