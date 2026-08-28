package ke.co.jodam.insurance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ke.co.jodam.insurance.dto.inquiry.AssignInquiryRequest;
import ke.co.jodam.insurance.dto.inquiry.InquiryRequest;
import ke.co.jodam.insurance.dto.inquiry.InquiryResponse;
import ke.co.jodam.insurance.entity.InquiryStatus;
import ke.co.jodam.insurance.entity.InsuranceInquiry;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.InsuranceInquiryRepository;
import ke.co.jodam.insurance.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class InquiryService {

    private final InsuranceInquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public InquiryService(
            InsuranceInquiryRepository inquiryRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper
    ) {
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public InquiryResponse createInquiry(InquiryRequest request, String username) {
        User customer = getUser(username);

        requireRole(customer, "CUSTOMER", "Only customers can submit insurance inquiries");

        InsuranceInquiry inquiry = new InsuranceInquiry();
        inquiry.setCustomer(customer);
        inquiry.setInsuranceType(request.getInsuranceType());
        inquiry.setDescription(request.getDescription());

        if (request.getApplicationData() != null) {
            try {
                inquiry.setApplicationData(
                        objectMapper.writeValueAsString(request.getApplicationData())
                );
            } catch (JsonProcessingException exception) {
                throw new IllegalArgumentException("Invalid motor application data", exception);
            }
        }

        inquiry.setStatus(InquiryStatus.NEW);
        return toResponse(inquiryRepository.save(inquiry));
    }

    @Transactional
    public InquiryResponse assignInquiry(
            Long inquiryId,
            AssignInquiryRequest request,
            String adminUsername
    ) {
        User admin = getUser(adminUsername);
        requireStaffProcessingRole(admin, "Only agents or administrators can assign inquiries");

        InsuranceInquiry inquiry = getInquiry(inquiryId);

        if (inquiry.getStatus() != InquiryStatus.NEW) {
            throw new IllegalStateException("Only new inquiries can be assigned");
        }

        User assignee = userRepository
                .findByUsername(request.getAgentUsername())
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        requireStaffProcessingRole(
                assignee,
                "Selected user must be an agent or administrator"
        );

        inquiry.setAssignedAgent(assignee);
        inquiry.setStatus(InquiryStatus.ASSIGNED);

        return toResponse(inquiryRepository.save(inquiry));
    }

    @Transactional
    public InquiryResponse startProcessing(Long inquiryId, String username) {
        User staff = getUser(username);
        requireStaffProcessingRole(staff, "Only agents or administrators can process inquiries");

        InsuranceInquiry inquiry = getInquiry(inquiryId);
        ensureAssignedToCurrentStaff(inquiry, staff);

        if (inquiry.getStatus() != InquiryStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Only assigned inquiries can be started for processing"
            );
        }

        inquiry.setStatus(InquiryStatus.UNDER_REVIEW);
        return toResponse(inquiryRepository.save(inquiry));
    }

    @Transactional(readOnly = true)
    public List<InquiryResponse> getMyInquiries(String customerUsername) {
        User customer = getUser(customerUsername);
        requireRole(customer, "CUSTOMER", "Only customers can view their inquiries");

        return inquiryRepository
                .findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InquiryResponse> getNewInquiries(String adminUsername) {
        User admin = getUser(adminUsername);
        requireStaffProcessingRole(admin, "Only agents or administrators can view unassigned inquiries");

        return inquiryRepository
                .findByStatusOrderByCreatedAtDesc(InquiryStatus.NEW)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InquiryResponse> getAllInquiries(String adminUsername) {
        User admin = getUser(adminUsername);
        requireStaffProcessingRole(admin, "Only agents or administrators can view all inquiries");

        return inquiryRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InquiryResponse> getAssignedInquiries(String username) {
        User staff = getUser(username);
        requireStaffProcessingRole(staff, "Only agents or administrators can view assigned inquiries");

        return inquiryRepository
                .findByAssignedAgentOrderByCreatedAtDesc(staff)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InquiryResponse getInquiryById(Long inquiryId, String username) {
        User user = getUser(username);
        InsuranceInquiry inquiry = getInquiry(inquiryId);

        boolean isAdmin = hasRole(user, "ADMIN");
        boolean isCustomer = hasRole(user, "CUSTOMER");
        boolean isAgent = hasRole(user, "AGENT");

        boolean ownsInquiry = inquiry.getCustomer() != null
                && inquiry.getCustomer().getId().equals(user.getId());

        boolean isAssignedStaff = inquiry.getAssignedAgent() != null
                && inquiry.getAssignedAgent().getId().equals(user.getId());

        if (isAdmin || isAgent || (isCustomer && ownsInquiry)) {
            return toResponse(inquiry);
        }

        throw new IllegalStateException("You are not authorized to view this inquiry");
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

    private boolean hasStaffProcessingRole(User user) {
        return hasRole(user, "AGENT") || hasRole(user, "ADMIN");
    }

    private void requireRole(User user, String roleName, String errorMessage) {
        if (!hasRole(user, roleName)) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private void requireStaffProcessingRole(User user, String errorMessage) {
        if (!hasStaffProcessingRole(user)) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private void ensureAssignedToCurrentStaff(InsuranceInquiry inquiry, User staff) {
        if (inquiry.getAssignedAgent() == null
                || !inquiry.getAssignedAgent().getId().equals(staff.getId())) {
            throw new IllegalStateException("This inquiry is not assigned to you");
        }
    }

    private InquiryResponse toResponse(InsuranceInquiry inquiry) {
        String assignedAgentUsername = null;
        if (inquiry.getAssignedAgent() != null) {
            assignedAgentUsername = inquiry.getAssignedAgent().getUsername();
        }

        Map<String, Object> applicationData = Map.of();
        if (inquiry.getApplicationData() != null && !inquiry.getApplicationData().isBlank()) {
            try {
                applicationData = objectMapper.readValue(
                        inquiry.getApplicationData(),
                        new TypeReference<Map<String, Object>>() {}
                );
            } catch (JsonProcessingException ignored) {
                // Preserve access to the inquiry even if legacy JSON is malformed.
            }
        }

        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getInsuranceType(),
                inquiry.getDescription(),
                applicationData,
                inquiry.getStatus(),
                inquiry.getCustomer().getUsername(),
                assignedAgentUsername,
                inquiry.getCreatedAt(),
                inquiry.getUpdatedAt()
        );
    }
}
