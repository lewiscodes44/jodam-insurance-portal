package ke.co.jodam.insurance.controller;

import jakarta.validation.Valid;
import ke.co.jodam.insurance.dto.quotation.QuotationRequest;
import ke.co.jodam.insurance.dto.quotation.QuotationResponse;
import ke.co.jodam.insurance.service.QuotationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
public class QuotationController {

    private final QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @PostMapping("/inquiry/{inquiryId}")
    public ResponseEntity<QuotationResponse> createQuotation(
            @PathVariable Long inquiryId,
            @Valid @RequestBody QuotationRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                quotationService.createQuotation(
                        inquiryId,
                        request,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{quotationId}")
    public ResponseEntity<QuotationResponse> updateQuotation(
            @PathVariable Long quotationId,
            @Valid @RequestBody QuotationRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                quotationService.updateQuotation(
                        quotationId,
                        request,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{quotationId}/send")
    public ResponseEntity<QuotationResponse> sendQuotation(
            @PathVariable Long quotationId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                quotationService.sendQuotation(
                        quotationId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/inquiry/{inquiryId}")
    public ResponseEntity<QuotationResponse> getQuotationByInquiry(
            @PathVariable Long inquiryId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                quotationService.getQuotationByInquiry(
                        inquiryId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<QuotationResponse>> getMyQuotations(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                quotationService.getMyQuotations(authentication.getName())
        );
    }

    @PostMapping("/{quotationId}/accept")
    public ResponseEntity<QuotationResponse> acceptQuotation(
            @PathVariable Long quotationId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                quotationService.acceptQuotation(
                        quotationId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{quotationId}/reject")
    public ResponseEntity<QuotationResponse> rejectQuotation(
            @PathVariable Long quotationId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                quotationService.rejectQuotation(
                        quotationId,
                        authentication.getName()
                )
        );
    }
}
