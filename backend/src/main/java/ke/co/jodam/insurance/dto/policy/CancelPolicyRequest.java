package ke.co.jodam.insurance.dto.policy;

import jakarta.validation.constraints.NotBlank;

public class CancelPolicyRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}