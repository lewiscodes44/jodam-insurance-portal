package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.reporting.AnalyticsSummaryResponse;
import ke.co.jodam.insurance.dto.reporting.DashboardSummaryResponse;
import ke.co.jodam.insurance.dto.reporting.InquiryReportResponse;
import ke.co.jodam.insurance.dto.reporting.PaymentReportResponse;
import ke.co.jodam.insurance.dto.reporting.PerformanceReportResponse;
import ke.co.jodam.insurance.dto.reporting.PolicyReportResponse;
import ke.co.jodam.insurance.entity.InquiryStatus;
import ke.co.jodam.insurance.entity.InsuranceInquiry;
import ke.co.jodam.insurance.entity.Payment;
import ke.co.jodam.insurance.entity.PaymentStatus;
import ke.co.jodam.insurance.entity.Policy;
import ke.co.jodam.insurance.entity.PolicyStatus;
import ke.co.jodam.insurance.repository.InsuranceInquiryRepository;
import ke.co.jodam.insurance.repository.PaymentRepository;
import ke.co.jodam.insurance.repository.PolicyRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReportingService {

    private final InsuranceInquiryRepository inquiryRepository;
    private final PolicyRepository policyRepository;
    private final PaymentRepository paymentRepository;

    public ReportingService(
            InsuranceInquiryRepository inquiryRepository,
            PolicyRepository policyRepository,
            PaymentRepository paymentRepository
    ) {
        this.inquiryRepository = inquiryRepository;
        this.policyRepository = policyRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {

        long totalInquiries = inquiryRepository.count();

        long newInquiries =
                inquiryRepository.countByStatus(InquiryStatus.NEW);

        long assignedInquiries =
                inquiryRepository.countByStatus(InquiryStatus.ASSIGNED);

        long quotedInquiries =
                countByStatuses(
                        InquiryStatus.QUOTATION_DRAFT,
                        InquiryStatus.QUOTATION_SENT,
                        InquiryStatus.QUOTED
                );

        long acceptedInquiries =
                countByStatuses(
                        InquiryStatus.CUSTOMER_ACCEPTED,
                        InquiryStatus.ACCEPTED
                );

        long rejectedInquiries =
                countByStatuses(
                        InquiryStatus.CUSTOMER_DECLINED,
                        InquiryStatus.REJECTED
                );

        long convertedInquiries =
                countByStatuses(
                        InquiryStatus.POLICY_PENDING_PAYMENT,
                        InquiryStatus.CONVERTED
                );

        long totalPolicies = policyRepository.count();

        long pendingPaymentPolicies =
                policyRepository.countByStatus(
                        PolicyStatus.PENDING_PAYMENT
                );

        long activePolicies =
                policyRepository.countByStatus(
                        PolicyStatus.ACTIVE
                );

        long expiredPolicies =
                policyRepository.countByStatus(
                        PolicyStatus.EXPIRED
                );

        long cancelledPolicies =
                policyRepository.countByStatus(
                        PolicyStatus.CANCELLED
                );

        long totalPayments = paymentRepository.count();

        long pendingPayments =
                paymentRepository.countByStatus(
                        PaymentStatus.PENDING
                );

        long processingPayments =
                paymentRepository.countByStatus(
                        PaymentStatus.PROCESSING
                );

        long completedPayments =
                paymentRepository.countByStatus(
                        PaymentStatus.COMPLETED
                );

        long failedPayments =
                paymentRepository.countByStatus(
                        PaymentStatus.FAILED
                );

        long cancelledPayments =
                paymentRepository.countByStatus(
                        PaymentStatus.CANCELLED
                );

        BigDecimal totalCompletedPaymentAmount =
                paymentRepository.sumCompletedPaymentAmount();

        if (totalCompletedPaymentAmount == null) {
            totalCompletedPaymentAmount = BigDecimal.ZERO;
        }

        return new DashboardSummaryResponse(
                totalInquiries,
                newInquiries,
                assignedInquiries,
                quotedInquiries,
                acceptedInquiries,
                rejectedInquiries,
                convertedInquiries,
                totalPolicies,
                pendingPaymentPolicies,
                activePolicies,
                expiredPolicies,
                cancelledPolicies,
                totalPayments,
                pendingPayments,
                processingPayments,
                completedPayments,
                failedPayments,
                cancelledPayments,
                totalCompletedPaymentAmount
        );
    }

    @Transactional(readOnly = true)
    public List<PolicyReportResponse> getPolicyReport() {

        return policyRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toPolicyReportResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentReportResponse> getPaymentReport() {

        return paymentRepository
                .findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                )
                .stream()
                .map(this::toPaymentReportResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InquiryReportResponse> getInquiryReport() {

        return inquiryRepository
                .findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                )
                .stream()
                .map(this::toInquiryReportResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getAnalyticsSummary() {

        BigDecimal totalPolicyPremiumValue =
                BigDecimal.ZERO;

        BigDecimal activePolicyPremiumValue =
                BigDecimal.ZERO;

        BigDecimal cancelledPolicyPremiumValue =
                BigDecimal.ZERO;

        List<Policy> policies =
                policyRepository.findAll();

        for (Policy policy : policies) {

            BigDecimal premium =
                    safeAmount(policy.getPremiumAmount());

            totalPolicyPremiumValue =
                    totalPolicyPremiumValue.add(premium);

            if (policy.getStatus() == PolicyStatus.ACTIVE) {
                activePolicyPremiumValue =
                        activePolicyPremiumValue.add(premium);
            }

            if (policy.getStatus() == PolicyStatus.CANCELLED) {
                cancelledPolicyPremiumValue =
                        cancelledPolicyPremiumValue.add(premium);
            }
        }

        BigDecimal completedPaymentValue =
                BigDecimal.ZERO;

        BigDecimal pendingPaymentValue =
                BigDecimal.ZERO;

        BigDecimal processingPaymentValue =
                BigDecimal.ZERO;

        BigDecimal failedPaymentValue =
                BigDecimal.ZERO;

        long totalCompletedPayments = 0;
        long totalPendingPayments = 0;
        long totalProcessingPayments = 0;
        long totalFailedPayments = 0;

        List<Payment> payments =
                paymentRepository.findAll();

        for (Payment payment : payments) {

            BigDecimal amount =
                    safeAmount(payment.getAmount());

            if (payment.getStatus()
                    == PaymentStatus.COMPLETED) {

                completedPaymentValue =
                        completedPaymentValue.add(amount);

                totalCompletedPayments++;
            }

            if (payment.getStatus()
                    == PaymentStatus.PENDING) {

                pendingPaymentValue =
                        pendingPaymentValue.add(amount);

                totalPendingPayments++;
            }

            if (payment.getStatus()
                    == PaymentStatus.PROCESSING) {

                processingPaymentValue =
                        processingPaymentValue.add(amount);

                totalProcessingPayments++;
            }

            if (payment.getStatus()
                    == PaymentStatus.FAILED) {

                failedPaymentValue =
                        failedPaymentValue.add(amount);

                totalFailedPayments++;
            }
        }

        BigDecimal outstandingPaymentValue =
                pendingPaymentValue.add(
                        processingPaymentValue
                );

        long totalInquiries =
                inquiryRepository.count();

        long convertedInquiries =
                countByStatuses(
                        InquiryStatus.POLICY_PENDING_PAYMENT,
                        InquiryStatus.CONVERTED
                );

        BigDecimal conversionRate =
                calculateConversionRate(
                        convertedInquiries,
                        totalInquiries
                );

        return new AnalyticsSummaryResponse(
                totalPolicyPremiumValue,
                activePolicyPremiumValue,
                cancelledPolicyPremiumValue,
                completedPaymentValue,
                pendingPaymentValue,
                processingPaymentValue,
                outstandingPaymentValue,
                failedPaymentValue,
                totalCompletedPayments,
                totalPendingPayments,
                totalProcessingPayments,
                totalFailedPayments,
                totalInquiries,
                convertedInquiries,
                conversionRate
        );
    }

    private long countByStatuses(InquiryStatus... statuses) {
        long count = 0;
        for (InquiryStatus status : statuses) {
            count += inquiryRepository.countByStatus(status);
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<PerformanceReportResponse>
    getMonthlyPerformanceReport() {

        List<Policy> policies =
                policyRepository.findAll();

        List<Payment> payments =
                paymentRepository.findAll();

        List<InsuranceInquiry> inquiries =
                inquiryRepository.findAll();

        Map<YearMonth, PerformanceAccumulator> periods =
                new TreeMap<>();

        for (Policy policy : policies) {

            YearMonth period =
                    toYearMonth(policy.getCreatedAt());

            PerformanceAccumulator accumulator =
                    periods.computeIfAbsent(
                            period,
                            ignored -> new PerformanceAccumulator()
                    );

            accumulator.policyCount++;

            accumulator.premiumValue =
                    accumulator.premiumValue.add(
                            safeAmount(
                                    policy.getPremiumAmount()
                            )
                    );
        }

        for (Payment payment : payments) {

            if (payment.getStatus()
                    != PaymentStatus.COMPLETED) {
                continue;
            }

            YearMonth period =
                    toYearMonth(payment.getCreatedAt());

            PerformanceAccumulator accumulator =
                    periods.computeIfAbsent(
                            period,
                            ignored -> new PerformanceAccumulator()
                    );

            accumulator.completedPaymentCount++;

            accumulator.completedPaymentValue =
                    accumulator.completedPaymentValue.add(
                            safeAmount(
                                    payment.getAmount()
                            )
                    );
        }

        for (InsuranceInquiry inquiry : inquiries) {

            YearMonth period =
                    toYearMonth(inquiry.getCreatedAt());

            PerformanceAccumulator accumulator =
                    periods.computeIfAbsent(
                            period,
                            ignored -> new PerformanceAccumulator()
                    );

            accumulator.inquiryCount++;

            if (inquiry.getStatus()
                    == InquiryStatus.POLICY_PENDING_PAYMENT
                    || inquiry.getStatus()
                    == InquiryStatus.CONVERTED) {

                accumulator.convertedInquiryCount++;
            }
        }

        List<PerformanceReportResponse> response =
                new ArrayList<>();

        for (Map.Entry<
                YearMonth,
                PerformanceAccumulator> entry
                : periods.entrySet()) {

            PerformanceAccumulator accumulator =
                    entry.getValue();

            response.add(
                    toPerformanceResponse(
                            entry.getKey().toString(),
                            accumulator
                    )
            );
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<PerformanceReportResponse>
    getAnnualPerformanceReport() {

        List<PerformanceReportResponse> monthly =
                getMonthlyPerformanceReport();

        Map<Integer, PerformanceAccumulator> annual =
                new TreeMap<>();

        for (PerformanceReportResponse month
                : monthly) {

            int year =
                    Integer.parseInt(
                            month.getPeriod().substring(0, 4)
                    );

            PerformanceAccumulator accumulator =
                    annual.computeIfAbsent(
                            year,
                            ignored -> new PerformanceAccumulator()
                    );

            accumulator.policyCount +=
                    month.getPolicyCount();

            accumulator.premiumValue =
                    accumulator.premiumValue.add(
                            safeAmount(
                                    month.getPremiumValue()
                            )
                    );

            accumulator.completedPaymentCount +=
                    month.getCompletedPaymentCount();

            accumulator.completedPaymentValue =
                    accumulator.completedPaymentValue.add(
                            safeAmount(
                                    month.getCompletedPaymentValue()
                            )
                    );

            accumulator.inquiryCount +=
                    month.getInquiryCount();

            accumulator.convertedInquiryCount +=
                    month.getConvertedInquiryCount();
        }

        List<PerformanceReportResponse> response =
                new ArrayList<>();

        for (Map.Entry<
                Integer,
                PerformanceAccumulator> entry
                : annual.entrySet()) {

            PerformanceAccumulator accumulator =
                    entry.getValue();

            response.add(
                    toPerformanceResponse(
                            String.valueOf(entry.getKey()),
                            accumulator
                    )
            );
        }

        return response;
    }

    private PerformanceReportResponse
    toPerformanceResponse(
            String period,
            PerformanceAccumulator accumulator
    ) {
        return new PerformanceReportResponse(
                period,
                accumulator.policyCount,
                accumulator.premiumValue,
                accumulator.completedPaymentCount,
                accumulator.completedPaymentValue,
                accumulator.inquiryCount,
                accumulator.convertedInquiryCount,
                calculateConversionRate(
                        accumulator.convertedInquiryCount,
                        accumulator.inquiryCount
                )
        );
    }

    private YearMonth toYearMonth(
            LocalDateTime dateTime
    ) {
        return YearMonth.from(dateTime);
    }

    private BigDecimal calculateConversionRate(
            long convertedInquiries,
            long totalInquiries
    ) {
        if (totalInquiries == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(convertedInquiries)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(totalInquiries),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal safeAmount(
            BigDecimal amount
    ) {
        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }

    private PolicyReportResponse toPolicyReportResponse(
            Policy policy
    ) {
        return new PolicyReportResponse(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getInquiry() == null
                        ? null
                        : policy.getInquiry().getId(),
                policy.getQuotation() == null
                        ? null
                        : policy.getQuotation().getId(),
                policy.getCustomer() == null
                        ? null
                        : policy.getCustomer().getUsername(),
                policy.getAgent() == null
                        ? null
                        : policy.getAgent().getUsername(),
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

    private PaymentReportResponse toPaymentReportResponse(
            Payment payment
    ) {
        Policy policy = payment.getPolicy();

        return new PaymentReportResponse(
                payment.getId(),
                policy == null
                        ? null
                        : policy.getId(),
                policy == null
                        ? null
                        : policy.getPolicyNumber(),
                policy == null || policy.getCustomer() == null
                        ? null
                        : policy.getCustomer().getUsername(),
                policy == null || policy.getAgent() == null
                        ? null
                        : policy.getAgent().getUsername(),
                payment.getAmount(),
                payment.getPhoneNumber(),
                payment.getTransactionReference(),
                payment.getCheckoutRequestId(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    private InquiryReportResponse toInquiryReportResponse(
            InsuranceInquiry inquiry
    ) {
        return new InquiryReportResponse(
                inquiry.getId(),
                inquiry.getInsuranceType(),
                inquiry.getDescription(),
                inquiry.getStatus(),
                inquiry.getCustomer() == null
                        ? null
                        : inquiry.getCustomer().getUsername(),
                inquiry.getAssignedAgent() == null
                        ? null
                        : inquiry.getAssignedAgent().getUsername(),
                inquiry.getCreatedAt(),
                inquiry.getUpdatedAt()
        );
    }

    private static class PerformanceAccumulator {

        private long policyCount;
        private BigDecimal premiumValue =
                BigDecimal.ZERO;

        private long completedPaymentCount;
        private BigDecimal completedPaymentValue =
                BigDecimal.ZERO;

        private long inquiryCount;
        private long convertedInquiryCount;
    }
}