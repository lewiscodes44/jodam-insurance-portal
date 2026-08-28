package ke.co.jodam.insurance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MpesaConfig {

    @Value("${mpesa.consumer-key:}")
    private String consumerKey;

    @Value("${mpesa.consumer-secret:}")
    private String consumerSecret;

    @Value("${mpesa.passkey:}")
    private String passkey;

    @Value("${mpesa.shortcode:}")
    private String shortcode;

    @Value("${mpesa.callback-url:}")
    private String callbackUrl;

    @Value("${mpesa.oauth-url:https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials}")
    private String oauthUrl;

    @Value("${mpesa.stk-push-url:https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest}")
    private String stkPushUrl;

    @Value("${mpesa.stk-push-query-url:https://sandbox.safaricom.co.ke/mpesa/stkpushquery/v1/query}")
    private String stkPushQueryUrl;

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getPasskey() {
        return passkey;
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getOauthUrl() {
        return oauthUrl;
    }

    public String getStkPushUrl() {
        return stkPushUrl;
    }

    public String getStkPushQueryUrl() {
        return stkPushQueryUrl;
    }
}