package ke.co.jodam.insurance.dto.policy;

import ke.co.jodam.insurance.entity.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PolicyResponse {

    private Long id;
    private String policyNumber;
    private Long inquiryId;
    private Long quotationId;
    private String customerUsername;
    private String agentUsername;
    private String insuranceType;
    private BigDecimal premiumAmount;
    private String coverageDetails;
    private LocalDate startDate;
    private LocalDate endDate;
    private PolicyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PolicyResponse() {
    }

    public PolicyResponse(
            Long id,
            String policyNumber,
            Long inquiryId,
            Long quotationId,
            String customerUsername,
            String agentUsername,
            String insuranceType,
            BigDecimal premiumAmount,
            String coverageDetails,
            LocalDate startDate,
            LocalDate endDate,
            PolicyStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.inquiryId = inquiryId;
        this.quotationId = quotationId;
        this.customerUsername = customerUsername;
        this.agentUsername = agentUsername;
        this.insuranceType = insuranceType;
        this.premiumAmount = premiumAmount;
        this.coverageDetails = coverageDetails;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public Long getInquiryId() {
        return inquiryId;
    }

    public Long getQuotationId() {
        return quotationId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getAgentUsername() {
        return agentUsername;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public String getCoverageDetails() {
        return coverageDetails;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}