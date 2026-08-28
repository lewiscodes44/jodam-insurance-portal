package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.claim.ClaimDecisionRequest;
import ke.co.jodam.insurance.dto.claim.ClaimRequest;
import ke.co.jodam.insurance.dto.claim.ClaimResponse;
import ke.co.jodam.insurance.entity.Claim;
import ke.co.jodam.insurance.entity.ClaimStatus;
import ke.co.jodam.insurance.entity.Policy;
import ke.co.jodam.insurance.entity.PolicyStatus;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.ClaimRepository;
import ke.co.jodam.insurance.repository.PolicyRepository;
import ke.co.jodam.insurance.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;

    public ClaimService(
            ClaimRepository claimRepository,
            PolicyRepository policyRepository,
            UserRepository userRepository
    ) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ClaimResponse submitClaim(
            Long policyId,
            ClaimRequest request,
            String customerUsername
    ) {
        User customer = getUser(customerUsername);

        if (!hasRole(customer, "CUSTOMER")) {
            throw new IllegalStateException(
                    "Only customers can submit claims"
            );
        }

        Policy policy = getPolicy(policyId);

        if (!ownsPolicy(policy, customer)) {
            throw new IllegalStateException(
                    "You are not authorized to submit a claim for this policy"
            );
        }

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Claims can only be submitted for active policies"
            );
        }

        if (policy.getStartDate() == null
                || policy.getEndDate() == null) {
            throw new IllegalStateException(
                    "Policy coverage dates are missing"
            );
        }

        if (request.getIncidentDate() == null) {
            throw new IllegalArgumentException(
                    "Incident date is required"
            );
        }

        if (request.getIncidentDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Incident date cannot be in the future"
            );
        }

        if (request.getIncidentDate().isBefore(policy.getStartDate())
                || request.getIncidentDate().isAfter(policy.getEndDate())) {
            throw new IllegalArgumentException(
                    "Incident date must fall within the policy coverage period"
            );
        }

        Claim claim = new Claim();

        claim.setClaimNumber(generateClaimNumber());
        claim.setPolicy(policy);
        claim.setAssignedAgent(policy.getAgent());
        claim.setIncidentDate(request.getIncidentDate());
        claim.setDescription(request.getDescription().trim());
        claim.setClaimedAmount(request.getClaimedAmount());
        claim.setStatus(ClaimStatus.SUBMITTED);

        Claim savedClaim = claimRepository.save(claim);

        return toResponse(savedClaim);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyClaims(
            String customerUsername
    ) {
        User customer = getUser(customerUsername);

        if (!hasRole(customer, "CUSTOMER")) {
            throw new IllegalStateException(
                    "Only customers can view their claims"
            );
        }

        return claimRepository
                .findByPolicy_CustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClaimResponse getClaimById(
            Long claimId,
            String customerUsername
    ) {
        User customer = getUser(customerUsername);

        if (!hasRole(customer, "CUSTOMER")) {
            throw new IllegalStateException(
                    "Only customers can view claims through this endpoint"
            );
        }

        Claim claim = getClaim(claimId);

        if (!ownsClaim(claim, customer)) {
            throw new IllegalStateException(
                    "You are not authorized to view this claim"
            );
        }

        return toResponse(claim);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getAssignedClaims(
            String agentUsername
    ) {
        User agent = getUser(agentUsername);

        if (!hasRole(agent, "AGENT")) {
            throw new IllegalStateException(
                    "Only agents can access assigned claims"
            );
        }

        return claimRepository
                .findByAssignedAgentOrderByCreatedAtDesc(agent)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims(
            String username
    ) {
        User user = getUser(username);

        if (!hasRole(user, "ADMIN")) {
            throw new IllegalStateException(
                    "Only administrators can access all claims"
            );
        }

        return claimRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClaimResponse moveToUnderReview(
            Long claimId,
            String username
    ) {
        User user = getUser(username);

        requireStaffDecisionAccess(user);

        Claim claim = getClaim(claimId);

        ensureAuthorizedForClaim(claim, user);

        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only submitted claims can be moved to under review"
            );
        }

        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claim.setReviewedAt(LocalDateTime.now());

        Claim updatedClaim = claimRepository.save(claim);

        return toResponse(updatedClaim);
    }

    @Transactional
    public ClaimResponse approveClaim(
            Long claimId,
            ClaimDecisionRequest request,
            String username
    ) {
        User user = getUser(username);

        requireStaffDecisionAccess(user);

        Claim claim = getClaim(claimId);

        ensureAuthorizedForClaim(claim, user);

        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Only claims under review can be approved"
            );
        }

        if (request == null
                || request.getApprovedAmount() == null) {
            throw new IllegalArgumentException(
                    "Approved amount is required"
            );
        }

        BigDecimal approvedAmount =
                request.getApprovedAmount();

        if (approvedAmount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Approved amount must be greater than zero"
            );
        }

        if (claim.getClaimedAmount() == null) {
            throw new IllegalStateException(
                    "Claimed amount is missing"
            );
        }

        if (approvedAmount.compareTo(
                claim.getClaimedAmount()
        ) > 0) {
            throw new IllegalArgumentException(
                    "Approved amount cannot exceed claimed amount"
            );
        }

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setApprovedAmount(approvedAmount);
        claim.setDecisionReason(
                normalizeOptionalReason(
                        request.getDecisionReason()
                )
        );

        Claim updatedClaim = claimRepository.save(claim);

        return toResponse(updatedClaim);
    }

    @Transactional
    public ClaimResponse rejectClaim(
            Long claimId,
            ClaimDecisionRequest request,
            String username
    ) {
        User user = getUser(username);

        requireStaffDecisionAccess(user);

        Claim claim = getClaim(claimId);

        ensureAuthorizedForClaim(claim, user);

        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Only claims under review can be rejected"
            );
        }

        if (request == null
                || request.getDecisionReason() == null
                || request.getDecisionReason().isBlank()) {
            throw new IllegalArgumentException(
                    "Decision reason is required when rejecting a claim"
            );
        }

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setApprovedAmount(null);
        claim.setDecisionReason(
                request.getDecisionReason().trim()
        );

        Claim updatedClaim = claimRepository.save(claim);

        return toResponse(updatedClaim);
    }

    @Transactional
    public ClaimResponse settleClaim(
            Long claimId,
            String username
    ) {
        User user = getUser(username);

        requireStaffDecisionAccess(user);

        Claim claim = getClaim(claimId);

        ensureAuthorizedForClaim(claim, user);

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only approved claims can be settled"
            );
        }

        if (claim.getApprovedAmount() == null
                || claim.getApprovedAmount().signum() <= 0) {
            throw new IllegalStateException(
                    "Approved claim amount must be greater than zero before settlement"
            );
        }

        claim.setStatus(ClaimStatus.SETTLED);
        claim.setSettledAt(LocalDateTime.now());

        Claim updatedClaim = claimRepository.save(claim);

        return toResponse(updatedClaim);
    }

    @Transactional
    public ClaimResponse closeClaim(
            Long claimId,
            String username
    ) {
        User user = getUser(username);

        requireStaffDecisionAccess(user);

        Claim claim = getClaim(claimId);

        ensureAuthorizedForClaim(claim, user);

        if (claim.getStatus() != ClaimStatus.SETTLED) {
            throw new IllegalStateException(
                    "Only settled claims can be closed"
            );
        }

        claim.setStatus(ClaimStatus.CLOSED);
        claim.setClosedAt(LocalDateTime.now());

        Claim updatedClaim = claimRepository.save(claim);

        return toResponse(updatedClaim);
    }

    private User getUser(
            String username
    ) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );
    }

    private Policy getPolicy(
            Long policyId
    ) {
        return policyRepository
                .findById(policyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Policy not found"
                        )
                );
    }

    private Claim getClaim(
            Long claimId
    ) {
        return claimRepository
                .findById(claimId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Claim not found"
                        )
                );
    }

    private boolean ownsPolicy(
            Policy policy,
            User customer
    ) {
        return policy.getCustomer() != null
                && policy.getCustomer()
                .getId()
                .equals(customer.getId());
    }

    private boolean ownsClaim(
            Claim claim,
            User customer
    ) {
        return claim.getPolicy() != null
                && ownsPolicy(
                claim.getPolicy(),
                customer
        );
    }

    private boolean isAssignedAgent(
            Claim claim,
            User agent
    ) {
        return claim.getAssignedAgent() != null
                && claim.getAssignedAgent()
                .getId()
                .equals(agent.getId());
    }

    private boolean hasRole(
            User user,
            String roleName
    ) {
        return user.getRoles()
                .stream()
                .anyMatch(role ->
                        roleName.equals(
                                role.getName()
                        )
                );
    }

    private void requireStaffDecisionAccess(
            User user
    ) {
        boolean isAdmin =
                hasRole(user, "ADMIN");

        boolean isAgent =
                hasRole(user, "AGENT");

        if (!isAdmin && !isAgent) {
            throw new IllegalStateException(
                    "Only agents and administrators can process claims"
            );
        }
    }

    private void ensureAuthorizedForClaim(
            Claim claim,
            User user
    ) {
        if (hasRole(user, "ADMIN")) {
            return;
        }

        if (hasRole(user, "AGENT")
                && isAssignedAgent(claim, user)) {
            return;
        }

        throw new IllegalStateException(
                "You are not authorized to process this claim"
        );
    }

    private String normalizeOptionalReason(
            String reason
    ) {
        if (reason == null
                || reason.isBlank()) {
            return null;
        }

        return reason.trim();
    }

    private String generateClaimNumber() {
        String date =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.BASIC_ISO_DATE
                        );

        String uniquePart =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10)
                        .toUpperCase();

        return "JODAM-CLM-"
                + date
                + "-"
                + uniquePart;
    }

    private ClaimResponse toResponse(
            Claim claim
    ) {
        Policy policy = claim.getPolicy();
        User customer = policy.getCustomer();
        User agent = claim.getAssignedAgent();

        return new ClaimResponse(
                claim.getId(),
                claim.getClaimNumber(),
                policy.getId(),
                policy.getPolicyNumber(),
                customer == null
                        ? null
                        : customer.getUsername(),
                agent == null
                        ? null
                        : agent.getUsername(),
                claim.getIncidentDate(),
                claim.getDescription(),
                claim.getClaimedAmount(),
                claim.getStatus(),
                claim.getDecisionReason(),
                claim.getApprovedAmount(),
                claim.getReviewedAt(),
                claim.getSettledAt(),
                claim.getClosedAt(),
                claim.getCreatedAt(),
                claim.getUpdatedAt()
        );
    }
}