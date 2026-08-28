package ke.co.jodam.insurance.dto.payment;

public class MpesaStkPushQueryRequest {

    private String BusinessShortCode;
    private String Password;
    private String Timestamp;
    private String CheckoutRequestID;

    public String getBusinessShortCode() {
        return BusinessShortCode;
    }

    public void setBusinessShortCode(
            String businessShortCode
    ) {
        BusinessShortCode = businessShortCode;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(
            String password
    ) {
        Password = password;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(
            String timestamp
    ) {
        Timestamp = timestamp;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public void setCheckoutRequestID(
            String checkoutRequestID
    ) {
        CheckoutRequestID = checkoutRequestID;
    }
}