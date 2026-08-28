package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.quotation.QuotationRequest;
import ke.co.jodam.insurance.dto.quotation.QuotationResponse;
import ke.co.jodam.insurance.entity.InquiryStatus;
import ke.co.jodam.insurance.entity.InsuranceInquiry;
import ke.co.jodam.insurance.entity.Quotation;
import ke.co.jodam.insurance.entity.QuotationStatus;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.InsuranceInquiryRepository;
import ke.co.jodam.insurance.repository.QuotationRepository;
import ke.co.jodam.insurance.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final InsuranceInquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public QuotationService(
            QuotationRepository quotationRepository,
            InsuranceInquiryRepository inquiryRepository,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.quotationRepository = quotationRepository;
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Creates the quotation as a DRAFT. Sending is deliberately a separate
     * operation so staff can save, review and then explicitly send it.
     */
    @Transactional
    public QuotationResponse createQuotation(
            Long inquiryId,
            QuotationRequest request,
            String staffUsername
    ) {
        User staff = getUser(staffUsername);
        requireStaffProcessingRole(staff, "Only agents or administrators can create quotations");

        InsuranceInquiry inquiry = getInquiry(inquiryId);
        claimOrVerifyInquiry(inquiry, staff);

        if (inquiry.getStatus() != InquiryStatus.NEW
                && inquiry.getStatus() != InquiryStatus.ASSIGNED
                && inquiry.getStatus() != InquiryStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Only new, assigned, or under-review inquiries can have a quotation prepared"
            );
        }

        inquiry.setStatus(InquiryStatus.UNDER_REVIEW);

        if (quotationRepository.findByInquiry(inquiry).isPresent()) {
            throw new IllegalStateException(
                    "A quotation already exists for this inquiry"
            );
        }

        validateDatesAndAmounts(request);

        Quotation quotation = new Quotation();
        quotation.setInquiry(inquiry);
        quotation.setAgent(staff);
        applyRequest(quotation, request);
        quotation.setQuoteReference(generateQuoteReference(inquiryId));
        quotation.setStatus(QuotationStatus.DRAFT);
        quotation.calculateTotalPayable();

        Quotation savedQuotation = quotationRepository.save(quotation);

        inquiry.setStatus(InquiryStatus.QUOTATION_DRAFT);
        inquiryRepository.save(inquiry);

        return toResponse(savedQuotation);
    }

    @Transactional
    public QuotationResponse updateQuotation(
            Long quotationId,
            QuotationRequest request,
            String staffUsername
    ) {
        User staff = getUser(staffUsername);
        requireStaffProcessingRole(staff, "Only agents or administrators can edit quotations");

        Quotation quotation = getQuotation(quotationId);
        ensureAssignedToCurrentStaff(quotation.getInquiry(), staff);

        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new IllegalStateException("Only draft quotations can be edited");
        }

        validateDatesAndAmounts(request);
        applyRequest(quotation, request);
        quotation.calculateTotalPayable();

        Quotation savedQuotation = quotationRepository.save(quotation);
        return toResponse(savedQuotation);
    }

    @Transactional
    public QuotationResponse sendQuotation(Long quotationId, String staffUsername) {
        User staff = getUser(staffUsername);
        requireStaffProcessingRole(staff, "Only agents or administrators can send quotations");

        Quotation quotation = getQuotation(quotationId);
        ensureAssignedToCurrentStaff(quotation.getInquiry(), staff);

        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new IllegalStateException("Only draft quotations can be sent");
        }

        if (quotation.getValidUntil().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Quotation validity date has already passed");
        }

        if (quotation.getProposedStartDate() != null
                && quotation.getProposedEndDate() != null
                && quotation.getProposedEndDate().isBefore(quotation.getProposedStartDate())) {
            throw new IllegalStateException("Proposed policy end date cannot be before start date");
        }

        quotation.setStatus(QuotationStatus.SENT);
        quotation.setSentAt(LocalDateTime.now());

        InsuranceInquiry inquiry = quotation.getInquiry();
        inquiry.setStatus(InquiryStatus.QUOTATION_SENT);
        inquiryRepository.save(inquiry);
        notificationService.createQuotationSentNotification(quotation);

        return toResponse(quotationRepository.save(quotation));
    }

    @Transactional(readOnly = true)
    public QuotationResponse getQuotationByInquiry(Long inquiryId, String username) {
        User user = getUser(username);
        InsuranceInquiry inquiry = getInquiry(inquiryId);

        Quotation quotation = findQuotation(inquiry);

        if (hasRole(user, "ADMIN")) {
            return toResponse(quotation);
        }

        if (hasRole(user, "CUSTOMER")
                && inquiry.getCustomer().getId().equals(user.getId())) {
            if (quotation.getStatus() == QuotationStatus.DRAFT) {
                throw new IllegalStateException(
                        "This quotation is still being prepared and is not yet available"
                );
            }
            return toResponse(quotation);
        }

        if ((hasRole(user, "AGENT") || hasRole(user, "ADMIN"))
                && inquiry.getAssignedAgent() != null
                && inquiry.getAssignedAgent().getId().equals(user.getId())) {
            return toResponse(findQuotation(inquiry));
        }

        throw new IllegalStateException("You are not authorized to view this quotation");
    }

    @Transactional(readOnly = true)
    public List<QuotationResponse> getMyQuotations(String customerUsername) {
        User customer = getUser(customerUsername);

        if (!hasRole(customer, "CUSTOMER")) {
            throw new IllegalStateException("Only customers can view their quotations");
        }

        return quotationRepository
                .findByInquiryCustomerId(customer.getId())
                .stream()
                .filter(quotation -> quotation.getStatus() != QuotationStatus.DRAFT)
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public QuotationResponse acceptQuotation(Long quotationId, String customerUsername) {
        Quotation quotation = getQuotation(quotationId);
        InsuranceInquiry inquiry = quotation.getInquiry();
        User customer = getUser(customerUsername);

        if (!hasRole(customer, "CUSTOMER")) {
            throw new IllegalStateException("Only customers can accept quotations");
        }

        if (!inquiry.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to accept this quotation");
        }

        if (quotation.getStatus() != QuotationStatus.SENT
                || (inquiry.getStatus() != InquiryStatus.QUOTATION_SENT
                && inquiry.getStatus() != InquiryStatus.QUOTED)) {
            throw new IllegalStateException("Only sent quotations can be accepted");
        }

        quotation.setStatus(QuotationStatus.ACCEPTED);
        inquiry.setStatus(InquiryStatus.CUSTOMER_ACCEPTED);
        inquiryRepository.save(inquiry);

        return toResponse(quotationRepository.save(quotation));
    }

    @Transactional
    public QuotationResponse rejectQuotation(Long quotationId, String customerUsername) {
        Quotation quotation = getQuotation(quotationId);
        InsuranceInquiry inquiry = quotation.getInquiry();
        User customer = getUser(customerUsername);

        if (!hasRole(customer, "CUSTOMER")) {
            throw new IllegalStateException("Only customers can reject quotations");
        }

        if (!inquiry.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to reject this quotation");
        }

        if (quotation.getStatus() != QuotationStatus.SENT
                || (inquiry.getStatus() != InquiryStatus.QUOTATION_SENT
                && inquiry.getStatus() != InquiryStatus.QUOTED)) {
            throw new IllegalStateException("Only sent quotations can be declined");
        }

        quotation.setStatus(QuotationStatus.DECLINED);
        inquiry.setStatus(InquiryStatus.CUSTOMER_DECLINED);
        inquiryRepository.save(inquiry);

        return toResponse(quotationRepository.save(quotation));
    }

    private void applyRequest(Quotation quotation, QuotationRequest request) {
        quotation.setInsurer(request.getInsurer().trim());
        quotation.setProduct(request.getProduct().trim());
        quotation.setBasicPremium(request.getBasicPremium());
        quotation.setTrainingLevy(defaultAmount(request.getTrainingLevy()));
        quotation.setPhcfLevy(defaultAmount(request.getPhcfLevy()));
        quotation.setStampDuty(defaultAmount(request.getStampDuty()));
        quotation.setOtherCharges(defaultAmount(request.getOtherCharges()));
        quotation.setValidUntil(request.getValidUntil());
        quotation.setProposedStartDate(request.getProposedStartDate());
        quotation.setProposedEndDate(request.getProposedEndDate());
        quotation.setExcess(trimToNull(request.getExcess()));
        quotation.setSpecialTerms(trimToNull(request.getSpecialTerms()));
        quotation.setAgentNotes(trimToNull(request.getAgentNotes()));
        quotation.setCoverageDetails(
                trimToNull(request.getCoverageDetails()) != null
                        ? request.getCoverageDetails().trim()
                        : trimToNull(request.getSpecialTerms())
        );
    }

    private void validateDatesAndAmounts(QuotationRequest request) {
        if (request.getValidUntil() == null) {
            throw new IllegalArgumentException("Quotation validity date is required");
        }
        if (request.getValidUntil().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Quotation validity date cannot be in the past");
        }
        if (request.getProposedStartDate() != null
                && request.getProposedEndDate() != null
                && request.getProposedEndDate().isBefore(request.getProposedStartDate())) {
            throw new IllegalArgumentException("Proposed policy end date cannot be before start date");
        }
        if (request.getBasicPremium() == null
                || request.getBasicPremium().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Basic premium must be greater than zero");
        }
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String generateQuoteReference(Long inquiryId) {
        return "JODAM-Q-" + String.format("%06d", inquiryId) + "-" + System.currentTimeMillis() % 100000;
    }

    private Quotation findQuotation(InsuranceInquiry inquiry) {
        return quotationRepository
                .findByInquiry(inquiry)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found for this inquiry"));
    }

    private Quotation getQuotation(Long quotationId) {
        return quotationRepository
                .findById(quotationId)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));
    }

    private InsuranceInquiry getInquiry(Long inquiryId) {
        return inquiryRepository
                .findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Insurance inquiry not found"));
    }

    private User getUser(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles()
                .stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }

    private void requireStaffProcessingRole(User user, String message) {
        if (!hasRole(user, "AGENT") && !hasRole(user, "ADMIN")) {
            throw new IllegalStateException(message);
        }
    }

    private void ensureAssignedToCurrentStaff(InsuranceInquiry inquiry, User staff) {
        if (hasRole(staff, "ADMIN")) {
            return;
        }
        if (inquiry.getAssignedAgent() == null
                || !inquiry.getAssignedAgent().getId().equals(staff.getId())) {
            throw new IllegalStateException("This inquiry is not assigned to you");
        }
    }

    private void claimOrVerifyInquiry(InsuranceInquiry inquiry, User staff) {
        if (inquiry.getAssignedAgent() == null) {
            inquiry.setAssignedAgent(staff);
            return;
        }
        ensureAssignedToCurrentStaff(inquiry, staff);
    }

    private QuotationResponse toResponse(Quotation quotation) {
        QuotationResponse response = new QuotationResponse();
        response.setId(quotation.getId());
        response.setInquiryId(quotation.getInquiry().getId());
        response.setCustomerUsername(quotation.getInquiry().getCustomer().getUsername());
        response.setAgentUsername(quotation.getAgent().getUsername());
        response.setInsurer(quotation.getInsurer());
        response.setProduct(quotation.getProduct());
        response.setBasicPremium(quotation.getBasicPremium());
        response.setTrainingLevy(quotation.getTrainingLevy());
        response.setPhcfLevy(quotation.getPhcfLevy());
        response.setStampDuty(quotation.getStampDuty());
        response.setOtherCharges(quotation.getOtherCharges());
        response.setTotalPayable(quotation.getTotalPayable());
        response.setPremiumAmount(quotation.getPremiumAmount());
        response.setCoverageDetails(quotation.getCoverageDetails());
        response.setQuoteReference(quotation.getQuoteReference());
        response.setValidUntil(quotation.getValidUntil());
        response.setProposedStartDate(quotation.getProposedStartDate());
        response.setProposedEndDate(quotation.getProposedEndDate());
        response.setExcess(quotation.getExcess());
        response.setSpecialTerms(quotation.getSpecialTerms());
        response.setAgentNotes(quotation.getAgentNotes());
        response.setStatus(quotation.getStatus());
        response.setCreatedAt(quotation.getCreatedAt());
        response.setUpdatedAt(quotation.getUpdatedAt());
        response.setSentAt(quotation.getSentAt());
        return response;
    }
}
