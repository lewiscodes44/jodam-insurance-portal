package ke.co.jodam.insurance.controller;

import ke.co.jodam.insurance.dto.payment.MpesaCallbackRequest;
import ke.co.jodam.insurance.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/mpesa")
public class MpesaCallbackController {

    private final PaymentService paymentService;

    public MpesaCallbackController(
            PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(
            @RequestBody MpesaCallbackRequest callback
    ) {

        try {

            paymentService.processMpesaCallback(
                    callback
            );

            return ResponseEntity.ok().build();

        } catch (Exception exception) {

            System.err.println(
                    "M-PESA CALLBACK ERROR: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return ResponseEntity.ok().build();
        }
    }
}