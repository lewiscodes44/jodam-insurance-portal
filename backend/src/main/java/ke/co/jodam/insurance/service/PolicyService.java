package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.policy.CancelPolicyRequest;
import ke.co.jodam.insurance.dto.policy.PolicyRequest;
import ke.co.jodam.insurance.dto.policy.PolicyResponse;
import ke.co.jodam.insurance.dto.policy.RenewPolicyRequest;
import ke.co.jodam.insurance.entity.InsuranceInquiry;
import ke.co.jodam.insurance.entity.InquiryStatus;
import ke.co.jodam.insurance.entity.Policy;
import ke.co.jodam.insurance.entity.PolicyStatus;
import ke.co.jodam.insurance.entity.Quotation;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.InsuranceInquiryRepository;
import ke.co.jodam.insurance.repository.PolicyRepository;
import ke.co.jodam.insurance.repository.QuotationRepository;
import ke.co.jodam.insurance.repository.UserRepository;
import ke.co.jodam.insurance.entity.Payment;
import ke.co.jodam.insurance.entity.PaymentStatus;
import ke.co.jodam.insurance.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final QuotationRepository quotationRepository;
    private final InsuranceInquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public PolicyService(
            PolicyRepository policyRepository,
            QuotationRepository quotationRepository,
            InsuranceInquiryRepository inquiryRepository,
            UserRepository userRepository,
	        PaymentRepository paymentRepository,
            NotificationService notificationService
    ) {
        this.policyRepository = policyRepository;
        this.quotationRepository = quotationRepository;
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
	    this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PolicyResponse issuePolicy(
            Long quotationId,
            PolicyRequest request,
            String agentUsername
    ) {
        User agent = getUser(agentUsername);

        if (!hasRole(agent, "AGENT") && !hasRole(agent, "ADMIN")) {
            throw new IllegalStateException(
                    "Only agents or administrators can issue policies"
            );
        }

        Quotation quotation = quotationRepository
                .findById(quotationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Quotation not found"
                        )
                );

        InsuranceInquiry inquiry = quotation.getInquiry();

        if (!hasRole(agent, "ADMIN") && (inquiry.getAssignedAgent() == null
                || !inquiry.getAssignedAgent()
                .getId()
                .equals(agent.getId()))) {

            throw new IllegalStateException(
                    "This quotation is not assigned to you"
            );
        }

        if (inquiry.getStatus() != InquiryStatus.CUSTOMER_ACCEPTED
                && inquiry.getStatus() != InquiryStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Only accepted quotations can be converted into policies"
            );
        }

        if (policyRepository
                .findByQuotationId(quotationId)
                .isPresent()) {

            throw new IllegalStateException(
                    "A policy already exists for this quotation"
            );
        }

        LocalDate startDate = request.getStartDate();

        LocalDate endDate = startDate
                .plusMonths(request.getDurationMonths())
                .minusDays(1);

        Policy policy = new Policy();

        policy.setPolicyNumber(
                generatePolicyNumber(quotationId)
        );

        policy.setInquiry(inquiry);
        policy.setQuotation(quotation);
        policy.setCustomer(inquiry.getCustomer());
        policy.setAgent(agent);
        policy.setInsuranceType(inquiry.getInsuranceType());
        policy.setPremiumAmount(quotation.getPremiumAmount());
        policy.setCoverageDetails(quotation.getCoverageDetails());
        policy.setStartDate(startDate);
        policy.setEndDate(endDate);
        policy.setStatus(PolicyStatus.PENDING_PAYMENT);

        Policy savedPolicy = policyRepository.save(policy);

        inquiry.setStatus(InquiryStatus.POLICY_PENDING_PAYMENT);

        inquiryRepository.save(inquiry);
        notificationService.createPolicyIssuedNotification(savedPolicy);

        return toResponse(savedPolicy);
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> getMyPolicies(
            String username
    ) {
        User user = getUser(username);

        List<Policy> policies;

        if (hasRole(user, "CUSTOMER")) {

            policies = policyRepository
                    .findByCustomerOrderByCreatedAtDesc(user);

        } else if (hasRole(user, "AGENT")) {

            policies = policyRepository
                    .findByAgentOrderByCreatedAtDesc(user);

        } else {

            throw new IllegalStateException(
                    "You are not authorized to view policies"
            );
        }

        return policies.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> getAllPolicies(
            String username
    ) {
        User user = getUser(username);

        if (!hasRole(user, "ADMIN")) {
            throw new IllegalStateException(
                    "Only administrators can view all policies"
            );
        }

        return policyRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PolicyResponse getPolicyById(
            Long policyId,
            String username
    ) {
        User user = getUser(username);

        Policy policy = getPolicy(policyId);

        if (hasRole(user, "ADMIN")) {
            return toResponse(policy);
        }

        boolean ownsPolicy =
                policy.getCustomer() != null
                        && policy.getCustomer()
                        .getId()
                        .equals(user.getId());

        boolean managesPolicy =
                policy.getAgent() != null
                        && policy.getAgent()
                        .getId()
                        .equals(user.getId());

        if (hasRole(user, "CUSTOMER") && ownsPolicy) {
            return toResponse(policy);
        }

        if (hasRole(user, "AGENT") && managesPolicy) {
            return toResponse(policy);
        }

        throw new IllegalStateException(
                "You are not authorized to view this policy"
        );
    }

    @Transactional
    public PolicyResponse cancelPolicy(
            Long policyId,
            CancelPolicyRequest request,
            String customerUsername
    ) {
        User customer = getUser(customerUsername);

        if (!hasRole(customer, "CUSTOMER")) {
            throw new IllegalStateException(
                    "Only customers can cancel policies"
            );
        }

        Policy policy = getPolicy(policyId);

        if (!ownsPolicy(policy, customer)) {
            throw new IllegalStateException(
                    "You are not authorized to cancel this policy"
            );
        }

        if (policy.getStatus() != PolicyStatus.ACTIVE
                && policy.getStatus() != PolicyStatus.PENDING_PAYMENT) {

            throw new IllegalStateException(
                    "Only active or pending-payment policies can be cancelled"
            );
        }

        policy.setStatus(
                PolicyStatus.CANCELLED
        );

        policy.setCancellationReason(
                request.getReason()
        );

        policy.setCancelledAt(
                LocalDateTime.now()
        );

        List<Payment> policyPayments =
                paymentRepository.findByPolicyId(
                        policyId
                );

        for (Payment payment : policyPayments) {

            if (payment.getStatus() == PaymentStatus.PENDING
                    || payment.getStatus() == PaymentStatus.PROCESSING) {

                payment.setStatus(
                        PaymentStatus.CANCELLED
                );
            }
        }

        paymentRepository.saveAll(
                policyPayments
        );

        return toResponse(
                policyRepository.save(policy)
        );
    }

    @Transactional
    public PolicyResponse renewPolicy(
            Long policyId,
            RenewPolicyRequest request,
            String customerUsername
    ) {
        User customer = getUser(customerUsername);

        if (!hasRole(customer, "CUSTOMER")) {
            throw new IllegalStateException(
                    "Only customers can renew policies"
            );
        }

        Policy policy = getPolicy(policyId);

        if (!ownsPolicy(policy, customer)) {
            throw new IllegalStateException(
                    "You are not authorized to renew this policy"
            );
        }

        if (policy.getStatus() != PolicyStatus.ACTIVE
                && policy.getStatus() != PolicyStatus.EXPIRED) {

            throw new IllegalStateException(
                    "Only active or expired policies can be renewed"
            );
        }

        if (policy.getEndDate() == null) {
            throw new IllegalStateException(
                    "Policy end date is missing"
            );
        }

        LocalDate requestedEndDate =
                request.getNewEndDate();

        if (requestedEndDate == null) {
            throw new IllegalArgumentException(
                    "New end date is required"
            );
        }

        if (!requestedEndDate.isAfter(
                policy.getEndDate()
        )) {

            throw new IllegalArgumentException(
                    "New end date must be later than the current policy end date"
            );
        }

        policy.setEndDate(requestedEndDate);

        policy.setStatus(
                PolicyStatus.PENDING_PAYMENT
        );

        return toResponse(
                policyRepository.save(policy)
        );
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

    private boolean hasRole(
            User user,
            String roleName
    ) {
        return user.getRoles()
                .stream()
                .anyMatch(role ->
                        roleName.equals(role.getName())
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

    private String generatePolicyNumber(
            Long quotationId
    ) {
        String date = LocalDate.now()
                .format(
                        DateTimeFormatter.BASIC_ISO_DATE
                );

        return "JODAM-"
                + date
                + "-"
                + String.format(
                "%06d",
                quotationId
        );
    }

    private PolicyResponse toResponse(
            Policy policy
    ) {
        return new PolicyResponse(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getInquiry().getId(),
                policy.getQuotation().getId(),
                policy.getCustomer().getUsername(),
                policy.getAgent().getUsername(),
                policy.getInsuranceType(),
                policy.getPremiumAmount(),
                policy.getCoverageDetails(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
        );
    }
}