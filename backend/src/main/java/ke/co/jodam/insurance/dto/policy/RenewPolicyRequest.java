package ke.co.jodam.insurance.dto.policy;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RenewPolicyRequest {

    @NotNull(message = "New end date is required")
    @Future(message = "New end date must be in the future")
    private LocalDate newEndDate;

    public LocalDate getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(LocalDate newEndDate) {
        this.newEndDate = newEndDate;
    }
}