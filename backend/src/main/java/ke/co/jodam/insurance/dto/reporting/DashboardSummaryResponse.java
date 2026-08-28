package ke.co.jodam.insurance.dto.reporting;

import java.math.BigDecimal;

public class DashboardSummaryResponse {

    private long totalInquiries;
    private long newInquiries;
    private long assignedInquiries;
    private long quotedInquiries;
    private long acceptedInquiries;
    private long rejectedInquiries;
    private long convertedInquiries;

    private long totalPolicies;
    private long pendingPaymentPolicies;
    private long activePolicies;
    private long expiredPolicies;
    private long cancelledPolicies;

    private long totalPayments;
    private long pendingPayments;
    private long processingPayments;
    private long completedPayments;
    private long failedPayments;
    private long cancelledPayments;

    private BigDecimal totalCompletedPaymentAmount;

    public DashboardSummaryResponse(
            long totalInquiries,
            long newInquiries,
            long assignedInquiries,
            long quotedInquiries,
            long acceptedInquiries,
            long rejectedInquiries,
            long convertedInquiries,
            long totalPolicies,
            long pendingPaymentPolicies,
            long activePolicies,
            long expiredPolicies,
            long cancelledPolicies,
            long totalPayments,
            long pendingPayments,
            long processingPayments,
            long completedPayments,
            long failedPayments,
            long cancelledPayments,
            BigDecimal totalCompletedPaymentAmount
    ) {
        this.totalInquiries = totalInquiries;
        this.newInquiries = newInquiries;
        this.assignedInquiries = assignedInquiries;
        this.quotedInquiries = quotedInquiries;
        this.acceptedInquiries = acceptedInquiries;
        this.rejectedInquiries = rejectedInquiries;
        this.convertedInquiries = convertedInquiries;
        this.totalPolicies = totalPolicies;
        this.pendingPaymentPolicies = pendingPaymentPolicies;
        this.activePolicies = activePolicies;
        this.expiredPolicies = expiredPolicies;
        this.cancelledPolicies = cancelledPolicies;
        this.totalPayments = totalPayments;
        this.pendingPayments = pendingPayments;
        this.processingPayments = processingPayments;
        this.completedPayments = completedPayments;
        this.failedPayments = failedPayments;
        this.cancelledPayments = cancelledPayments;
        this.totalCompletedPaymentAmount = totalCompletedPaymentAmount;
    }

    public long getTotalInquiries() {
        return totalInquiries;
    }

    public long getNewInquiries() {
        return newInquiries;
    }

    public long getAssignedInquiries() {
        return assignedInquiries;
    }

    public long getQuotedInquiries() {
        return quotedInquiries;
    }

    public long getAcceptedInquiries() {
        return acceptedInquiries;
    }

    public long getRejectedInquiries() {
        return rejectedInquiries;
    }

    public long getConvertedInquiries() {
        return convertedInquiries;
    }

    public long getTotalPolicies() {
        return totalPolicies;
    }

    public long getPendingPaymentPolicies() {
        return pendingPaymentPolicies;
    }

    public long getActivePolicies() {
        return activePolicies;
    }

    public long getExpiredPolicies() {
        return expiredPolicies;
    }

    public long getCancelledPolicies() {
        return cancelledPolicies;
    }

    public long getTotalPayments() {
        return totalPayments;
    }

    public long getPendingPayments() {
        return pendingPayments;
    }

    public long getProcessingPayments() {
        return processingPayments;
    }

    public long getCompletedPayments() {
        return completedPayments;
    }

    public long getFailedPayments() {
        return failedPayments;
    }

    public long getCancelledPayments() {
        return cancelledPayments;
    }

    public BigDecimal getTotalCompletedPaymentAmount() {
        return totalCompletedPaymentAmount;
    }
}