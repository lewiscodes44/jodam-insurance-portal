package ke.co.jodam.insurance.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PaymentRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(?:254|0)7\\d{8}$",
            message = "Phone number must be a valid Kenyan mobile number"
    )
    private String phoneNumber;

    public PaymentRequest() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}