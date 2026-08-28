package ke.co.jodam.insurance.dto.claim;

import ke.co.jodam.insurance.entity.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClaimResponse {

    private Long id;
    private String claimNumber;
    private Long policyId;
    private String policyNumber;
    private String customerUsername;
    private String assignedAgentUsername;
    private LocalDate incidentDate;
    private String description;
    private BigDecimal claimedAmount;
    private ClaimStatus status;
    private String decisionReason;
    private BigDecimal approvedAmount;
    private LocalDateTime reviewedAt;
    private LocalDateTime settledAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClaimResponse() {
    }

    public ClaimResponse(
            Long id,
            String claimNumber,
            Long policyId,
            String policyNumber,
            String customerUsername,
            String assignedAgentUsername,
            LocalDate incidentDate,
            String description,
            BigDecimal claimedAmount,
            ClaimStatus status,
            String decisionReason,
            BigDecimal approvedAmount,
            LocalDateTime reviewedAt,
            LocalDateTime settledAt,
            LocalDateTime closedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.claimNumber = claimNumber;
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.customerUsername = customerUsername;
        this.assignedAgentUsername = assignedAgentUsername;
        this.incidentDate = incidentDate;
        this.description = description;
        this.claimedAmount = claimedAmount;
        this.status = status;
        this.decisionReason = decisionReason;
        this.approvedAmount = approvedAmount;
        this.reviewedAt = reviewedAt;
        this.settledAt = settledAt;
        this.closedAt = closedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getClaimNumber() {
        return claimNumber;
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

    public String getAssignedAgentUsername() {
        return assignedAgentUsername;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getClaimedAmount() {
        return claimedAmount;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public LocalDateTime getSettledAt() {
        return settledAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}