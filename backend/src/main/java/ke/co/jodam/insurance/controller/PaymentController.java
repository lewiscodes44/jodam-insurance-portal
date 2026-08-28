package ke.co.jodam.insurance.controller;

import jakarta.validation.Valid;

import ke.co.jodam.insurance.dto.payment.PaymentRequest;
import ke.co.jodam.insurance.dto.payment.PaymentResponse;
import ke.co.jodam.insurance.service.PaymentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }

    /**
     * Initiates payment for a customer's policy.
     */
    @PostMapping("/policy/{policyId}")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @PathVariable Long policyId,
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication
    ) {

        String customerUsername = authentication.getName();

        PaymentResponse response =
                paymentService.initiatePayment(
                        policyId,
                        request,
                        customerUsername
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Reconciles the latest payment status for a customer's payment.
     */
    @PostMapping("/{paymentId}/query")
    public ResponseEntity<PaymentResponse> queryPayment(
            @PathVariable Long paymentId,
            Authentication authentication
    ) {

        String customerUsername = authentication.getName();

        PaymentResponse response =
                paymentService.reconcilePayment(
                        paymentId,
                        customerUsername
                );

        return ResponseEntity.ok(response);
    }
}