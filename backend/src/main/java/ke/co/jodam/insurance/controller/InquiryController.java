package ke.co.jodam.insurance.controller;

import jakarta.validation.Valid;
import ke.co.jodam.insurance.dto.inquiry.AssignInquiryRequest;
import ke.co.jodam.insurance.dto.inquiry.InquiryRequest;
import ke.co.jodam.insurance.dto.inquiry.InquiryResponse;
import ke.co.jodam.insurance.service.InquiryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public ResponseEntity<InquiryResponse> createInquiry(
            @Valid @RequestBody InquiryRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                inquiryService.createInquiry(request, authentication.getName())
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<InquiryResponse>> getMyInquiries(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                inquiryService.getMyInquiries(authentication.getName())
        );
    }

    @GetMapping("/new")
    public ResponseEntity<List<InquiryResponse>> getNewInquiries(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                inquiryService.getNewInquiries(authentication.getName())
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<InquiryResponse>> getAllInquiries(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                inquiryService.getAllInquiries(authentication.getName())
        );
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<InquiryResponse>> getAssignedInquiries(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                inquiryService.getAssignedInquiries(authentication.getName())
        );
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponse> getInquiryById(
            @PathVariable Long inquiryId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                inquiryService.getInquiryById(inquiryId, authentication.getName())
        );
    }

    @PostMapping("/{inquiryId}/assign")
    public ResponseEntity<InquiryResponse> assignInquiry(
            @PathVariable Long inquiryId,
            @Valid @RequestBody AssignInquiryRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                inquiryService.assignInquiry(
                        inquiryId,
                        request,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{inquiryId}/start-processing")
    public ResponseEntity<InquiryResponse> startProcessing(
            @PathVariable Long inquiryId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                inquiryService.startProcessing(
                        inquiryId,
                        authentication.getName()
                )
        );
    }
}
