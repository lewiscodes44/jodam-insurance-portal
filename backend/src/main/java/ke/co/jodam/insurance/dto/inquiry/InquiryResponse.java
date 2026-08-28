package ke.co.jodam.insurance.dto.inquiry;

import ke.co.jodam.insurance.entity.InquiryStatus;

import java.time.LocalDateTime;
import java.util.Map;

public class InquiryResponse {

    private Long id;
    private String insuranceType;
    private String description;
    private Map<String, Object> applicationData;
    private InquiryStatus status;
    private String customerUsername;
    private String assignedAgentUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InquiryResponse() {}

    public InquiryResponse(Long id, String insuranceType, String description,
                           Map<String, Object> applicationData, InquiryStatus status,
                           String customerUsername, String assignedAgentUsername,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.insuranceType = insuranceType;
        this.description = description;
        this.applicationData = applicationData;
        this.status = status;
        this.customerUsername = customerUsername;
        this.assignedAgentUsername = assignedAgentUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getInsuranceType() { return insuranceType; }
    public String getDescription() { return description; }
    public Map<String, Object> getApplicationData() { return applicationData; }
    public InquiryStatus getStatus() { return status; }
    public String getCustomerUsername() { return customerUsername; }
    public String getAssignedAgentUsername() { return assignedAgentUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
