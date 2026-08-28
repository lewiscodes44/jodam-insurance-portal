package ke.co.jodam.insurance.controller;

import jakarta.validation.Valid;

import ke.co.jodam.insurance.dto.claim.ClaimDecisionRequest;
import ke.co.jodam.insurance.dto.claim.ClaimRequest;
import ke.co.jodam.insurance.dto.claim.ClaimResponse;
import ke.co.jodam.insurance.service.ClaimService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(
            ClaimService claimService
    ) {
        this.claimService = claimService;
    }

    @PostMapping("/policy/{policyId}")
    public ResponseEntity<ClaimResponse> submitClaim(
            @PathVariable Long policyId,
            @Valid @RequestBody ClaimRequest request,
            Authentication authentication
    ) {
        ClaimResponse response =
                claimService.submitClaim(
                        policyId,
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ClaimResponse>> getMyClaims(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.getMyClaims(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<ClaimResponse>> getAssignedClaims(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.getAssignedClaims(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClaimResponse>> getAllClaims(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.getAllClaims(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<ClaimResponse> getClaimById(
            @PathVariable Long claimId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.getClaimById(
                        claimId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{claimId}/review")
    public ResponseEntity<ClaimResponse> moveToUnderReview(
            @PathVariable Long claimId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.moveToUnderReview(
                        claimId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{claimId}/approve")
    public ResponseEntity<ClaimResponse> approveClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimDecisionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.approveClaim(
                        claimId,
                        request,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{claimId}/reject")
    public ResponseEntity<ClaimResponse> rejectClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimDecisionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.rejectClaim(
                        claimId,
                        request,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{claimId}/settle")
    public ResponseEntity<ClaimResponse> settleClaim(
            @PathVariable Long claimId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.settleClaim(
                        claimId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{claimId}/close")
    public ResponseEntity<ClaimResponse> closeClaim(
            @PathVariable Long claimId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                claimService.closeClaim(
                        claimId,
                        authentication.getName()
                )
        );
    }
}