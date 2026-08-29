package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.payment.MpesaCallbackRequest;
import ke.co.jodam.insurance.dto.payment.MpesaStkPushQueryResponse;
import ke.co.jodam.insurance.dto.payment.MpesaStkPushResponse;
import ke.co.jodam.insurance.dto.payment.PaymentRequest;
import ke.co.jodam.insurance.dto.payment.PaymentResponse;
import ke.co.jodam.insurance.entity.Payment;
import ke.co.jodam.insurance.entity.PaymentStatus;
import ke.co.jodam.insurance.entity.InquiryStatus;
import ke.co.jodam.insurance.entity.Policy;
import ke.co.jodam.insurance.entity.PolicyStatus;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.PaymentRepository;
import ke.co.jodam.insurance.repository.PolicyRepository;
import ke.co.jodam.insurance.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final MpesaService mpesaService;
    private final NotificationService notificationService;

    public PaymentService(
            PaymentRepository paymentRepository,
            PolicyRepository policyRepository,
            UserRepository userRepository,
            MpesaService mpesaService,
            NotificationService notificationService
    ) {
        this.paymentRepository =
                paymentRepository;

        this.policyRepository =
                policyRepository;

        this.userRepository =
                userRepository;

        this.mpesaService =
                mpesaService;

        this.notificationService =
                notificationService;
    }

    @Transactional
    public PaymentResponse initiatePayment(
            Long policyId,
            PaymentRequest request,
            String customerUsername
    ) {

        User customer =
                userRepository
                        .findByUsername(
                                customerUsername
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Authenticated user not found"
                                        )
                        );

        boolean isCustomer =
                customer
                        .getRoles()
                        .stream()
                        .anyMatch(
                                role ->
                                        "CUSTOMER".equals(
                                                role.getName()
                                        )
                        );

        if (!isCustomer) {

            throw new IllegalStateException(
                    "Only customers can initiate payments"
            );
        }

        Policy policy =
                policyRepository
                        .findById(
                                policyId
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Policy not found"
                                        )
                        );

        boolean ownsPolicy =
                policy.getCustomer() != null
                        && Objects.equals(
                        policy
                                .getCustomer()
                                .getId(),
                        customer.getId()
                );

        if (!ownsPolicy) {

            throw new IllegalStateException(
                    "You are not authorized to pay for this policy"
            );
        }

        if (policy.getStatus()
                != PolicyStatus.PENDING_PAYMENT) {

            throw new IllegalStateException(
                    "This policy is not awaiting payment"
            );
        }

        boolean pendingPaymentExists =
                paymentRepository
                        .findFirstByPolicyIdAndStatusOrderByIdDesc(
                                policyId,
                                PaymentStatus.PENDING
                        )
                        .isPresent();

        boolean processingPaymentExists =
                paymentRepository
                        .findFirstByPolicyIdAndStatusOrderByIdDesc(
                                policyId,
                                PaymentStatus.PROCESSING
                        )
                        .isPresent();

        if (pendingPaymentExists
                || processingPaymentExists) {

            throw new IllegalStateException(
                    "A payment is already in progress for this policy"
            );
        }

        Payment payment =
                new Payment();

        payment.setPolicy(
                policy
        );

        payment.setAmount(
                policy.getPremiumAmount()
        );

        payment.setPhoneNumber(
                request.getPhoneNumber()
        );

        payment.setStatus(
                PaymentStatus.PENDING
        );

        Payment savedPayment =
                paymentRepository
                        .saveAndFlush(
                                payment
                        );

        System.out.println(
                "PAYMENT CREATED"
        );

        System.out.println(
                "Payment ID: "
                        + savedPayment.getId()
        );

        System.out.println(
                "Policy ID: "
                        + savedPayment
                        .getPolicy()
                        .getId()
        );

        MpesaStkPushResponse stkResponse;

        try {

            stkResponse =
                    mpesaService
                            .initiateStkPush(
                                    savedPayment.getAmount(),
                                    savedPayment.getPhoneNumber(),
                                    savedPayment
                                            .getPolicy()
                                            .getPolicyNumber(),
                                    "Insurance policy payment"
                            );

        } catch (Exception exception) {

            savedPayment.setStatus(
                    PaymentStatus.FAILED
            );

            paymentRepository
                    .saveAndFlush(
                            savedPayment
                    );

            throw exception;
        }

        if (stkResponse == null
                || stkResponse
                .getCheckoutRequestID() == null
                || stkResponse
                .getCheckoutRequestID()
                .isBlank()) {

            savedPayment.setStatus(
                    PaymentStatus.FAILED
            );

            paymentRepository
                    .saveAndFlush(
                            savedPayment
                    );

            throw new IllegalStateException(
                    "M-PESA STK Push failed"
            );
        }

        String checkoutRequestId =
                stkResponse
                        .getCheckoutRequestID()
                        .trim();

        savedPayment.setCheckoutRequestId(
                checkoutRequestId
        );

        savedPayment.setStatus(
                PaymentStatus.PROCESSING
        );

        Payment updatedPayment =
                paymentRepository
                        .saveAndFlush(
                                savedPayment
                        );

        System.out.println(
                "PAYMENT READY FOR CALLBACK"
        );

        System.out.println(
                "Payment ID: "
                        + updatedPayment.getId()
        );

        System.out.println(
                "CheckoutRequestID: "
                        + updatedPayment
                        .getCheckoutRequestId()
        );

        System.out.println(
                "Status: "
                        + updatedPayment.getStatus()
        );

        return toResponse(
                updatedPayment
        );
    }

    @Transactional
    public PaymentResponse reconcilePayment(
            Long paymentId,
            String customerUsername
    ) {

        User customer =
                userRepository
                        .findByUsername(
                                customerUsername
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Authenticated user not found"
                                        )
                        );

        boolean isCustomer =
                customer
                        .getRoles()
                        .stream()
                        .anyMatch(
                                role ->
                                        "CUSTOMER".equals(
                                                role.getName()
                                        )
                        );

        if (!isCustomer) {

            throw new IllegalStateException(
                    "Only customers can reconcile payments"
            );
        }

        Payment payment =
                paymentRepository
                        .findById(
                                paymentId
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Payment not found"
                                        )
                        );

        Policy policy =
                payment.getPolicy();

        boolean ownsPolicy =
                policy != null
                        && policy.getCustomer() != null
                        && Objects.equals(
                        policy
                                .getCustomer()
                                .getId(),
                        customer.getId()
                );

        if (!ownsPolicy) {

            throw new IllegalStateException(
                    "You are not authorized to reconcile this payment"
            );
        }

        System.out.println(
                "========================================"
        );

        System.out.println(
                "PAYMENT RECONCILIATION STARTED"
        );

        System.out.println(
                "Payment ID: "
                        + payment.getId()
        );

        System.out.println(
                "Current Payment Status: "
                        + payment.getStatus()
        );

        System.out.println(
                "CheckoutRequestID: "
                        + payment
                        .getCheckoutRequestId()
        );

        if (payment.getStatus()
                == PaymentStatus.COMPLETED) {

            System.out.println(
                    "PAYMENT ALREADY COMPLETED"
            );

            System.out.println(
                    "========================================"
            );

            return toResponse(
                    payment
            );
        }

        if (payment.getStatus()
                == PaymentStatus.FAILED) {

            System.out.println(
                    "PAYMENT ALREADY FAILED"
            );

            System.out.println(
                    "========================================"
            );

            return toResponse(
                    payment
            );
        }

        if (payment.getCheckoutRequestId() == null
                || payment
                .getCheckoutRequestId()
                .isBlank()) {

            throw new IllegalStateException(
                    "This payment has no CheckoutRequestID to reconcile"
            );
        }

        MpesaStkPushQueryResponse queryResponse =
                mpesaService
                        .queryStkPushStatus(
                                payment
                                        .getCheckoutRequestId()
                        );

        String responseCode =
                queryResponse
                        .getResponseCode();

        if (responseCode != null
                && !"0".equals(
                responseCode.trim()
        )) {

            throw new IllegalStateException(
                    "M-PESA rejected the reconciliation query. ResponseCode: "
                            + responseCode
                            + ", ResponseDescription: "
                            + queryResponse
                            .getResponseDescription()
            );
        }

        String resultCode =
                queryResponse
                        .getResultCode();

        System.out.println(
                "PAYMENT RECONCILIATION RESULT"
        );

        System.out.println(
                "ResultCode: "
                        + resultCode
        );

        System.out.println(
                "ResultDesc: "
                        + queryResponse
                        .getResultDesc()
        );

        if (resultCode == null
                || resultCode.isBlank()) {

            System.out.println(
                    "M-PESA HAS NOT RETURNED A FINAL RESULT"
            );

            System.out.println(
                    "PAYMENT REMAINS PROCESSING"
            );

            System.out.println(
                    "========================================"
            );

            return toResponse(
                    payment
            );
        }

        if ("0".equals(
                resultCode.trim()
        )) {

            payment.setStatus(
                    PaymentStatus.COMPLETED
            );

            paymentRepository
                    .saveAndFlush(
                            payment
                    );

            if (policy.getStatus()
                    == PolicyStatus.PENDING_PAYMENT) {

                policy.setStatus(
                        PolicyStatus.ACTIVE
                );

                policyRepository
                        .saveAndFlush(
                                policy
                        );

                policy.getInquiry().setStatus(InquiryStatus.CONVERTED);

                System.out.println(
                        "POLICY ACTIVATED SUCCESSFULLY"
                );
            }

            notificationService
                    .createPaymentConfirmationNotifications(
                            payment
                    );

            System.out.println(
                    "PAYMENT RECONCILED AS COMPLETED"
            );

            System.out.println(
                    "Transaction Reference: "
                            + payment
                            .getTransactionReference()
            );

            System.out.println(
                    "========================================"
            );

            return toResponse(
                    payment
            );
        }

        if (payment.getStatus()
                == PaymentStatus.PROCESSING
                || payment.getStatus()
                == PaymentStatus.PENDING) {

            payment.setStatus(
                    PaymentStatus.FAILED
            );

            paymentRepository
                    .saveAndFlush(
                            payment
                    );

            System.out.println(
                    "PAYMENT RECONCILED AS FAILED"
            );

            System.out.println(
                    "========================================"
            );
        }

        return toResponse(
                payment
        );
    }

    @Transactional
    public boolean processMpesaCallback(
            MpesaCallbackRequest callback
    ) {

        if (callback == null
                || callback.getBody() == null
                || callback
                .getBody()
                .getStkCallback() == null) {

            System.out.println(
                    "M-PESA CALLBACK REJECTED: Invalid payload"
            );

            return false;
        }

        MpesaCallbackRequest.StkCallback stkCallback =
                callback
                        .getBody()
                        .getStkCallback();

        String checkoutRequestId =
                stkCallback
                        .getCheckoutRequestID();

        if (checkoutRequestId == null
                || checkoutRequestId.isBlank()) {

            System.out.println(
                    "M-PESA CALLBACK REJECTED: Missing CheckoutRequestID"
            );

            return false;
        }

        checkoutRequestId =
                checkoutRequestId.trim();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "M-PESA CALLBACK RECEIVED"
        );

        System.out.println(
                "CheckoutRequestID: "
                        + checkoutRequestId
        );

        System.out.println(
                "ResultCode: "
                        + stkCallback.getResultCode()
        );

        Optional<Payment> paymentOptional =
                paymentRepository
                        .findFirstByCheckoutRequestIdOrderByIdDesc(
                                checkoutRequestId
                        );

        if (paymentOptional.isEmpty()) {

            System.out.println(
                    "M-PESA CALLBACK: NO MATCHING PAYMENT FOUND"
            );

            System.out.println(
                    "Requested CheckoutRequestID: "
                            + checkoutRequestId
            );

            System.out.println(
                    "The callback will be acknowledged without throwing HTTP 500."
            );

            System.out.println(
                    "========================================"
            );

            return false;
        }

        Payment payment =
                paymentOptional.get();

        System.out.println(
                "MATCHING PAYMENT FOUND"
        );

        System.out.println(
                "Payment ID: "
                        + payment.getId()
        );

        System.out.println(
                "Stored CheckoutRequestID: "
                        + payment
                        .getCheckoutRequestId()
        );

        System.out.println(
                "Payment Status: "
                        + payment.getStatus()
        );

        System.out.println(
                "Policy ID: "
                        + payment
                        .getPolicy()
                        .getId()
        );

        if (payment.getStatus()
                == PaymentStatus.COMPLETED) {

            System.out.println(
                    "CALLBACK IGNORED: PAYMENT ALREADY COMPLETED"
            );

            System.out.println(
                    "========================================"
            );

            return true;
        }

        Integer resultCode =
                stkCallback.getResultCode();

        if (resultCode != null
                && resultCode == 0) {

            String transactionReference =
                    extractCallbackValue(
                            stkCallback,
                            "MpesaReceiptNumber"
                    );

            if (transactionReference != null
                    && !transactionReference.isBlank()) {

                transactionReference =
                        transactionReference.trim();

                Optional<Payment> existingReference =
                        paymentRepository
                                .findByTransactionReference(
                                        transactionReference
                                );

                if (existingReference.isPresent()
                        && !Objects.equals(
                        existingReference
                                .get()
                                .getId(),
                        payment.getId()
                )) {

                    System.out.println(
                            "M-PESA CALLBACK: Transaction reference already belongs to another payment"
                    );

                    System.out.println(
                            "========================================"
                    );

                    return false;
                }

                payment.setTransactionReference(
                        transactionReference
                );
            }

            payment.setStatus(
                    PaymentStatus.COMPLETED
            );

            paymentRepository
                    .saveAndFlush(
                            payment
                    );

            Policy policy =
                    payment.getPolicy();

            if (policy.getStatus()
                    == PolicyStatus.PENDING_PAYMENT) {

                policy.setStatus(
                        PolicyStatus.ACTIVE
                );

                policyRepository
                        .saveAndFlush(
                                policy
                        );

                policy.getInquiry().setStatus(InquiryStatus.CONVERTED);

                System.out.println(
                        "POLICY ACTIVATED SUCCESSFULLY"
                );

            } else {

                System.out.println(
                        "POLICY STATUS UNCHANGED: "
                                + policy.getStatus()
                );
            }

            notificationService
                    .createPaymentConfirmationNotifications(
                            payment
                    );

            System.out.println(
                    "PAYMENT COMPLETED SUCCESSFULLY"
            );

            System.out.println(
                    "Transaction Reference: "
                            + payment
                            .getTransactionReference()
            );

            System.out.println(
                    "========================================"
            );

            return true;

        } else {

            payment.setStatus(
                    PaymentStatus.FAILED
            );

            paymentRepository
                    .saveAndFlush(
                            payment
                    );

            System.out.println(
                    "PAYMENT MARKED AS FAILED"
            );

            System.out.println(
                    "ResultCode: "
                            + resultCode
            );

            System.out.println(
                    "========================================"
            );

            return true;
        }
    }

    private String extractCallbackValue(
            MpesaCallbackRequest.StkCallback stkCallback,
            String itemName
    ) {

        if (stkCallback
                .getCallbackMetadata() == null
                || stkCallback
                .getCallbackMetadata()
                .getItems() == null) {

            return null;
        }

        List<MpesaCallbackRequest.CallbackItem> items =
                stkCallback
                        .getCallbackMetadata()
                        .getItems();

        for (
                MpesaCallbackRequest.CallbackItem item
                : items
        ) {

            if (itemName.equals(
                    item.getName()
            )
                    && item.getValue() != null) {

                return String.valueOf(
                        item.getValue()
                );
            }
        }

        return null;
    }

    private PaymentResponse toResponse(
            Payment payment
    ) {

        return new PaymentResponse(
                payment.getId(),
                payment
                        .getPolicy()
                        .getId(),
                payment
                        .getPolicy()
                        .getPolicyNumber(),
                payment.getAmount(),
                payment.getPhoneNumber(),
                payment.getTransactionReference(),
                payment.getCheckoutRequestId(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
