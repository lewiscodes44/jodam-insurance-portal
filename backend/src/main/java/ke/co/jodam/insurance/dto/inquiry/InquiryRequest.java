package ke.co.jodam.insurance.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class InquiryRequest {

    @NotBlank(message = "Insurance type is required")
    @Size(max = 100, message = "Insurance type must not exceed 100 characters")
    private String insuranceType;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private Map<String, Object> applicationData;

    public InquiryRequest() {
    }

    public String getInsuranceType() { return insuranceType; }
    public void setInsuranceType(String insuranceType) { this.insuranceType = insuranceType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getApplicationData() { return applicationData; }
    public void setApplicationData(Map<String, Object> applicationData) { this.applicationData = applicationData; }
}
