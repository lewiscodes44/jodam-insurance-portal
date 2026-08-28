package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.config.MpesaConfig;
import ke.co.jodam.insurance.dto.payment.MpesaStkPushQueryRequest;
import ke.co.jodam.insurance.dto.payment.MpesaStkPushQueryResponse;
import ke.co.jodam.insurance.dto.payment.MpesaStkPushRequest;
import ke.co.jodam.insurance.dto.payment.MpesaStkPushResponse;
import ke.co.jodam.insurance.dto.payment.MpesaTokenResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class MpesaService {

    private final MpesaConfig mpesaConfig;
    private final RestClient restClient;

    public MpesaService(
            MpesaConfig mpesaConfig
    ) {
        this.mpesaConfig = mpesaConfig;
        this.restClient = RestClient
                .builder()
                .build();
    }

    public String getOauthUrl() {
        return mpesaConfig.getOauthUrl();
    }

    public String getStkPushUrl() {
        return mpesaConfig.getStkPushUrl();
    }

    public String generateAccessToken() {

        System.out.println(
                "MPESA: OAuth URL = "
                        + mpesaConfig.getOauthUrl()
        );

        System.out.println(
                "MPESA: Consumer key length = "
                        + (
                        mpesaConfig.getConsumerKey() != null
                                ? mpesaConfig
                                .getConsumerKey()
                                .length()
                                : "NULL"
                )
        );

        System.out.println(
                "MPESA: Consumer secret length = "
                        + (
                        mpesaConfig.getConsumerSecret() != null
                                ? mpesaConfig
                                .getConsumerSecret()
                                .length()
                                : "NULL"
                )
        );

        String credentials =
                mpesaConfig.getConsumerKey()
                        + ":"
                        + mpesaConfig.getConsumerSecret();

        String encodedCredentials =
                Base64.getEncoder()
                        .encodeToString(
                                credentials.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        );

        System.out.println(
                "MPESA: Basic credentials generated successfully"
        );

        try {

            MpesaTokenResponse response =
                    restClient
                            .get()
                            .uri(
                                    mpesaConfig.getOauthUrl()
                            )
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Basic "
                                            + encodedCredentials
                            )
                            .accept(
                                    MediaType.APPLICATION_JSON
                            )
                            .retrieve()
                            .body(
                                    MpesaTokenResponse.class
                            );

            if (response == null
                    || response.getAccessToken() == null
                    || response
                    .getAccessToken()
                    .isBlank()) {

                throw new IllegalStateException(
                        "M-PESA OAuth token was not returned"
                );
            }

            System.out.println(
                    "MPESA: OAuth access token generated successfully"
            );

            return response.getAccessToken();

        } catch (
                RestClientResponseException exception
        ) {

            System.out.println(
                    "MPESA: OAuth request failed"
            );

            System.out.println(
                    "MPESA: HTTP status = "
                            + exception.getStatusCode()
            );

            System.out.println(
                    "MPESA: Response body = "
                            + exception
                            .getResponseBodyAsString()
            );

            throw new IllegalStateException(
                    "M-PESA OAuth authentication failed: "
                            + exception
                            .getResponseBodyAsString(),
                    exception
            );
        }
    }

    public MpesaStkPushResponse initiateStkPush(
            BigDecimal amount,
            String phoneNumber,
            String accountReference,
            String transactionDescription
    ) {

        String accessToken =
                generateAccessToken();

        String normalizedPhoneNumber =
                normalizePhoneNumber(
                        phoneNumber
                );

        String timestamp =
                generateTimestamp();

        String password =
                generatePassword(
                        timestamp
                );

        MpesaStkPushRequest request =
                new MpesaStkPushRequest();

        request.setBusinessShortCode(
                mpesaConfig.getShortcode()
        );

        request.setPassword(
                password
        );

        request.setTimestamp(
                timestamp
        );

        request.setTransactionType(
                "CustomerPayBillOnline"
        );

        request.setAmount(
                amount
                        .setScale(0)
                        .toBigInteger()
                        .toString()
        );

        request.setPartyA(
                normalizedPhoneNumber
        );

        request.setPartyB(
                mpesaConfig.getShortcode()
        );

        request.setPhoneNumber(
                normalizedPhoneNumber
        );

        request.setCallBackURL(
                mpesaConfig.getCallbackUrl()
        );

        request.setAccountReference(
                accountReference
        );

        request.setTransactionDesc(
                transactionDescription
        );

        System.out.println(
                "MPESA: Initiating STK Push"
        );

        System.out.println(
                "MPESA: STK Push URL = "
                        + mpesaConfig.getStkPushUrl()
        );

        System.out.println(
                "MPESA: Business Shortcode = "
                        + mpesaConfig.getShortcode()
        );

        System.out.println(
                "MPESA: Amount = "
                        + request.getAmount()
        );

        System.out.println(
                "MPESA: PartyA = "
                        + request.getPartyA()
        );

        System.out.println(
                "MPESA: PartyB = "
                        + request.getPartyB()
        );

        System.out.println(
                "MPESA: PhoneNumber = "
                        + request.getPhoneNumber()
        );

        System.out.println(
                "MPESA: Callback URL = "
                        + request.getCallBackURL()
        );

        System.out.println(
                "MPESA: Account Reference = "
                        + request.getAccountReference()
        );

        System.out.println(
                "MPESA: Transaction Description = "
                        + request.getTransactionDesc()
        );

        try {

            MpesaStkPushResponse response =
                    restClient
                            .post()
                            .uri(
                                    mpesaConfig.getStkPushUrl()
                            )
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer "
                                            + accessToken
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .accept(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(
                                    request
                            )
                            .retrieve()
                            .body(
                                    MpesaStkPushResponse.class
                            );

            if (response == null) {

                throw new IllegalStateException(
                        "M-PESA returned an empty STK Push response"
                );
            }

            System.out.println(
                    "MPESA: STK Push HTTP request completed"
            );

            System.out.println(
                    "MPESA: MerchantRequestID = "
                            + response
                            .getMerchantRequestID()
            );

            System.out.println(
                    "MPESA: CheckoutRequestID = "
                            + response
                            .getCheckoutRequestID()
            );

            System.out.println(
                    "MPESA: ResponseCode = "
                            + response
                            .getResponseCode()
            );

            System.out.println(
                    "MPESA: ResponseDescription = "
                            + response
                            .getResponseDescription()
            );

            System.out.println(
                    "MPESA: CustomerMessage = "
                            + response
                            .getCustomerMessage()
            );

            if (!"0".equals(
                    response.getResponseCode()
            )) {

                throw new IllegalStateException(
                        "M-PESA rejected STK Push. ResponseCode: "
                                + response
                                .getResponseCode()
                                + ", ResponseDescription: "
                                + response
                                .getResponseDescription()
                );
            }

            if (response
                    .getCheckoutRequestID() == null
                    || response
                    .getCheckoutRequestID()
                    .isBlank()) {

                throw new IllegalStateException(
                        "M-PESA accepted the request but did not return a CheckoutRequestID"
                );
            }

            System.out.println(
                    "MPESA: STK Push accepted successfully"
            );

            return response;

        } catch (
                RestClientResponseException exception
        ) {

            System.out.println(
                    "MPESA: STK Push request failed"
            );

            System.out.println(
                    "MPESA: HTTP status = "
                            + exception.getStatusCode()
            );

            System.out.println(
                    "MPESA: Response body = "
                            + exception
                            .getResponseBodyAsString()
            );

            throw new IllegalStateException(
                    "M-PESA STK Push failed. Safaricom response: "
                            + exception
                            .getResponseBodyAsString(),
                    exception
            );
        }
    }

    public MpesaStkPushQueryResponse
    queryStkPushStatus(
            String checkoutRequestId
    ) {

        if (checkoutRequestId == null
                || checkoutRequestId.isBlank()) {

            throw new IllegalArgumentException(
                    "CheckoutRequestID is required"
            );
        }

        String accessToken =
                generateAccessToken();

        String timestamp =
                generateTimestamp();

        String password =
                generatePassword(
                        timestamp
                );

        MpesaStkPushQueryRequest request =
                new MpesaStkPushQueryRequest();

        request.setBusinessShortCode(
                mpesaConfig.getShortcode()
        );

        request.setPassword(
                password
        );

        request.setTimestamp(
                timestamp
        );

        request.setCheckoutRequestID(
                checkoutRequestId.trim()
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "MPESA: QUERYING STK PAYMENT STATUS"
        );

        System.out.println(
                "MPESA: Query URL = "
                        + mpesaConfig
                        .getStkPushQueryUrl()
        );

        System.out.println(
                "MPESA: CheckoutRequestID = "
                        + request
                        .getCheckoutRequestID()
        );

        try {

            MpesaStkPushQueryResponse response =
                    restClient
                            .post()
                            .uri(
                                    mpesaConfig
                                            .getStkPushQueryUrl()
                            )
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer "
                                            + accessToken
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .accept(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(
                                    request
                            )
                            .retrieve()
                            .body(
                                    MpesaStkPushQueryResponse.class
                            );

            if (response == null) {

                throw new IllegalStateException(
                        "M-PESA returned an empty STK query response"
                );
            }

            System.out.println(
                    "MPESA: Query ResponseCode = "
                            + response
                            .getResponseCode()
            );

            System.out.println(
                    "MPESA: Query ResponseDescription = "
                            + response
                            .getResponseDescription()
            );

            System.out.println(
                    "MPESA: Query ResultCode = "
                            + response
                            .getResultCode()
            );

            System.out.println(
                    "MPESA: Query ResultDesc = "
                            + response
                            .getResultDesc()
            );

            System.out.println(
                    "========================================"
            );

            return response;

        } catch (
                RestClientResponseException exception
        ) {

            System.out.println(
                    "MPESA: STK query request failed"
            );

            System.out.println(
                    "MPESA: HTTP status = "
                            + exception.getStatusCode()
            );

            System.out.println(
                    "MPESA: Response body = "
                            + exception
                            .getResponseBodyAsString()
            );

            throw new IllegalStateException(
                    "M-PESA STK query failed. Safaricom response: "
                            + exception
                            .getResponseBodyAsString(),
                    exception
            );
        }
    }

    private String generateTimestamp() {

        return LocalDateTime
                .now()
                .format(
                        DateTimeFormatter.ofPattern(
                                "yyyyMMddHHmmss"
                        )
                );
    }

    private String generatePassword(
            String timestamp
    ) {

        String passwordSource =
                mpesaConfig.getShortcode()
                        + mpesaConfig.getPasskey()
                        + timestamp;

        return Base64
                .getEncoder()
                .encodeToString(
                        passwordSource.getBytes(
                                StandardCharsets.UTF_8
                        )
                );
    }

    private String normalizePhoneNumber(
            String phoneNumber
    ) {

        if (phoneNumber == null
                || phoneNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Phone number is required"
            );
        }

        String normalized =
                phoneNumber
                        .trim()
                        .replace(
                                " ",
                                ""
                        )
                        .replace(
                                "-",
                                ""
                        );

        if (normalized.startsWith(
                "+254"
        )) {

            normalized =
                    "254"
                            + normalized
                            .substring(4);

        } else if (
                normalized.startsWith("07")
                        || normalized.startsWith("01")
        ) {

            normalized =
                    "254"
                            + normalized
                            .substring(1);

        } else if (
                normalized.startsWith("7")
                        || normalized.startsWith("1")
        ) {

            normalized =
                    "254"
                            + normalized;
        }

        if (!normalized.matches(
                "^254[17]\\d{8}$"
        )) {

            throw new IllegalArgumentException(
                    "Invalid Kenyan phone number"
            );
        }

        return normalized;
    }
}