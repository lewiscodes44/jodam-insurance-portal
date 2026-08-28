package ke.co.jodam.insurance.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MpesaCallbackRequest {

    @JsonProperty("Body")
    private CallbackBody body;

    public MpesaCallbackRequest() {
    }

    public CallbackBody getBody() {
        return body;
    }

    public void setBody(CallbackBody body) {
        this.body = body;
    }

    public static class CallbackBody {

        @JsonProperty("stkCallback")
        private StkCallback stkCallback;

        public CallbackBody() {
        }

        public StkCallback getStkCallback() {
            return stkCallback;
        }

        public void setStkCallback(StkCallback stkCallback) {
            this.stkCallback = stkCallback;
        }
    }

    public static class StkCallback {

        @JsonProperty("MerchantRequestID")
        private String merchantRequestID;

        @JsonProperty("CheckoutRequestID")
        private String checkoutRequestID;

        @JsonProperty("ResultCode")
        private Integer resultCode;

        @JsonProperty("ResultDesc")
        private String resultDesc;

        @JsonProperty("CallbackMetadata")
        private CallbackMetadata callbackMetadata;

        public StkCallback() {
        }

        public String getMerchantRequestID() {
            return merchantRequestID;
        }

        public void setMerchantRequestID(String merchantRequestID) {
            this.merchantRequestID = merchantRequestID;
        }

        public String getCheckoutRequestID() {
            return checkoutRequestID;
        }

        public void setCheckoutRequestID(String checkoutRequestID) {
            this.checkoutRequestID = checkoutRequestID;
        }

        public Integer getResultCode() {
            return resultCode;
        }

        public void setResultCode(Integer resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultDesc() {
            return resultDesc;
        }

        public void setResultDesc(String resultDesc) {
            this.resultDesc = resultDesc;
        }

        public CallbackMetadata getCallbackMetadata() {
            return callbackMetadata;
        }

        public void setCallbackMetadata(
                CallbackMetadata callbackMetadata
        ) {
            this.callbackMetadata = callbackMetadata;
        }
    }

    public static class CallbackMetadata {

        @JsonProperty("Item")
        private List<CallbackItem> items;

        public CallbackMetadata() {
        }

        public List<CallbackItem> getItems() {
            return items;
        }

        public void setItems(List<CallbackItem> items) {
            this.items = items;
        }
    }

    public static class CallbackItem {

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Value")
        private Object value;

        public CallbackItem() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}