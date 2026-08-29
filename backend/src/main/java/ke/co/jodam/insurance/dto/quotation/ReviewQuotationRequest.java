package ke.co.jodam.insurance.dto.quotation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class ReviewQuotationRequest {
    @NotBlank(message = "Tell us what you would like reviewed") @Size(max=3000) private String message;
    public String getMessage(){return message;} public void setMessage(String value){message=value;}
}
