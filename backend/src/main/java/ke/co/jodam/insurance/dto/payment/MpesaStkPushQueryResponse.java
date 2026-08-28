package ke.co.jodam.insurance.dto.payment;

public class MpesaStkPushQueryResponse {

    private String ResponseCode;
    private String ResponseDescription;
    private String MerchantRequestID;
    private String CheckoutRequestID;
    private String ResultCode;
    private String ResultDesc;

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(
            String responseCode
    ) {
        ResponseCode = responseCode;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(
            String responseDescription
    ) {
        ResponseDescription = responseDescription;
    }

    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public void setMerchantRequestID(
            String merchantRequestID
    ) {
        MerchantRequestID = merchantRequestID;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public void setCheckoutRequestID(
            String checkoutRequestID
    ) {
        CheckoutRequestID = checkoutRequestID;
    }

    public String getResultCode() {
        return ResultCode;
    }

    public void setResultCode(
            String resultCode
    ) {
        ResultCode = resultCode;
    }

    public String getResultDesc() {
        return ResultDesc;
    }

    public void setResultDesc(
            String resultDesc
    ) {
        ResultDesc = resultDesc;
    }
}