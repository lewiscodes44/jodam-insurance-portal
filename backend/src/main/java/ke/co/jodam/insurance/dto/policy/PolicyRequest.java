package ke.co.jodam.insurance.dto.policy;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PolicyRequest {
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;
    @NotNull(message = "Policy duration is required")
    @Min(value = 1, message = "Policy duration must be at least 1 month")
    @Max(value = 120, message = "Policy duration cannot exceed 120 months")
    private Integer durationMonths;
    @Size(max = 80)
    private String certificateNumber;
    @NotBlank(message = "Certificate class is required")
    @Size(max = 40)
    private String certificateClass;
    @Size(max = 100)
    private String valuationReference;
    private LocalDate valuationDate;
    @NotNull(message = "Document verification is required")
    @AssertTrue(message = "Confirm that the required documents have been verified")
    private Boolean documentsVerified;
    @Size(max = 5000)
    private String policyTerms;
    public PolicyRequest() {}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;}
    public Integer getDurationMonths(){return durationMonths;} public void setDurationMonths(Integer v){durationMonths=v;}
    public String getCertificateNumber(){return certificateNumber;} public void setCertificateNumber(String v){certificateNumber=v;}
    public String getCertificateClass(){return certificateClass;} public void setCertificateClass(String v){certificateClass=v;}
    public String getValuationReference(){return valuationReference;} public void setValuationReference(String v){valuationReference=v;}
    public LocalDate getValuationDate(){return valuationDate;} public void setValuationDate(LocalDate v){valuationDate=v;}
    public Boolean getDocumentsVerified(){return documentsVerified;} public void setDocumentsVerified(Boolean v){documentsVerified=v;}
    public String getPolicyTerms(){return policyTerms;} public void setPolicyTerms(String v){policyTerms=v;}
}
