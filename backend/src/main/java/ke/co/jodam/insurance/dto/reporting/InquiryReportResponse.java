package ke.co.jodam.insurance.dto.reporting;

import ke.co.jodam.insurance.entity.InquiryStatus;

import java.time.LocalDateTime;

public class InquiryReportResponse {

    private Long id;
    private String insuranceType;
    private String description;
    private InquiryStatus status;
    private String customerUsername;
    private String assignedAgentUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InquiryReportResponse() {
    }

    public InquiryReportResponse(
            Long id,
            String insuranceType,
            String description,
            InquiryStatus status,
            String customerUsername,
            String assignedAgentUsername,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.insuranceType = insuranceType;
        this.description = description;
        this.status = status;
        this.customerUsername = customerUsername;
        this.assignedAgentUsername = assignedAgentUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public String getDescription() {
        return description;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getAssignedAgentUsername() {
        return assignedAgentUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}