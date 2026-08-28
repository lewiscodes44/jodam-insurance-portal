package ke.co.jodam.insurance.controller;

import jakarta.validation.Valid;

import ke.co.jodam.insurance.dto.policy.CancelPolicyRequest;
import ke.co.jodam.insurance.dto.policy.PolicyRequest;
import ke.co.jodam.insurance.dto.policy.PolicyResponse;
import ke.co.jodam.insurance.dto.policy.RenewPolicyRequest;
import ke.co.jodam.insurance.service.PolicyService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(
            PolicyService policyService
    ) {
        this.policyService = policyService;
    }

    @PostMapping("/quotation/{quotationId}")
    public ResponseEntity<PolicyResponse> issuePolicy(
            @PathVariable Long quotationId,
            @Valid @RequestBody PolicyRequest request,
            Authentication authentication
    ) {
        PolicyResponse response =
                policyService.issuePolicy(
                        quotationId,
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PolicyResponse>>
    getMyPolicies(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                policyService.getMyPolicies(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<PolicyResponse>>
    getAllPolicies(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                policyService.getAllPolicies(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<PolicyResponse>
    getPolicyById(
            @PathVariable Long policyId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                policyService.getPolicyById(
                        policyId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{policyId}/cancel")
    public ResponseEntity<PolicyResponse>
    cancelPolicy(
            @PathVariable Long policyId,
            @Valid @RequestBody CancelPolicyRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                policyService.cancelPolicy(
                        policyId,
                        request,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{policyId}/renew")
    public ResponseEntity<PolicyResponse>
    renewPolicy(
            @PathVariable Long policyId,
            @Valid @RequestBody RenewPolicyRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                policyService.renewPolicy(
                        policyId,
                        request,
                        authentication.getName()
                )
        );
    }
}