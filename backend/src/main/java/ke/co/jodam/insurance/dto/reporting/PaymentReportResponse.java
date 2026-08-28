package ke.co.jodam.insurance.dto.reporting;

import ke.co.jodam.insurance.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentReportResponse {

    private Long id;
    private Long policyId;
    private String policyNumber;
    private String customerUsername;
    private String agentUsername;
    private BigDecimal amount;
    private String phoneNumber;
    private String transactionReference;
    private String checkoutRequestId;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentReportResponse() {
    }

    public PaymentReportResponse(
            Long id,
            Long policyId,
            String policyNumber,
            String customerUsername,
            String agentUsername,
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
        this.customerUsername = customerUsername;
        this.agentUsername = agentUsername;
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

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getAgentUsername() {
        return agentUsername;
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