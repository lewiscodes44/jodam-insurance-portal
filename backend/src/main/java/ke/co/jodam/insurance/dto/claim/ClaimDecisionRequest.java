package ke.co.jodam.insurance.dto.claim;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ClaimDecisionRequest {

    @DecimalMin(
            value = "0.01",
            message = "Approved amount must be greater than zero"
    )
    private BigDecimal approvedAmount;

    @Size(
            max = 2000,
            message = "Decision reason must not exceed 2000 characters"
    )
    private String decisionReason;

    public ClaimDecisionRequest() {
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }
}