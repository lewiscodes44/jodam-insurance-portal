package ke.co.jodam.insurance.dto.reporting;

import java.math.BigDecimal;

public class AnalyticsSummaryResponse {

    private BigDecimal totalPolicyPremiumValue;
    private BigDecimal activePolicyPremiumValue;
    private BigDecimal cancelledPolicyPremiumValue;

    private BigDecimal completedPaymentValue;
    private BigDecimal pendingPaymentValue;
    private BigDecimal processingPaymentValue;
    private BigDecimal outstandingPaymentValue;
    private BigDecimal failedPaymentValue;

    private long totalCompletedPayments;
    private long totalPendingPayments;
    private long totalProcessingPayments;
    private long totalFailedPayments;

    private long totalInquiries;
    private long convertedInquiries;
    private BigDecimal conversionRate;

    public AnalyticsSummaryResponse(
            BigDecimal totalPolicyPremiumValue,
            BigDecimal activePolicyPremiumValue,
            BigDecimal cancelledPolicyPremiumValue,
            BigDecimal completedPaymentValue,
            BigDecimal pendingPaymentValue,
            BigDecimal processingPaymentValue,
            BigDecimal outstandingPaymentValue,
            BigDecimal failedPaymentValue,
            long totalCompletedPayments,
            long totalPendingPayments,
            long totalProcessingPayments,
            long totalFailedPayments,
            long totalInquiries,
            long convertedInquiries,
            BigDecimal conversionRate
    ) {
        this.totalPolicyPremiumValue = totalPolicyPremiumValue;
        this.activePolicyPremiumValue = activePolicyPremiumValue;
        this.cancelledPolicyPremiumValue = cancelledPolicyPremiumValue;
        this.completedPaymentValue = completedPaymentValue;
        this.pendingPaymentValue = pendingPaymentValue;
        this.processingPaymentValue = processingPaymentValue;
        this.outstandingPaymentValue = outstandingPaymentValue;
        this.failedPaymentValue = failedPaymentValue;
        this.totalCompletedPayments = totalCompletedPayments;
        this.totalPendingPayments = totalPendingPayments;
        this.totalProcessingPayments = totalProcessingPayments;
        this.totalFailedPayments = totalFailedPayments;
        this.totalInquiries = totalInquiries;
        this.convertedInquiries = convertedInquiries;
        this.conversionRate = conversionRate;
    }

    public BigDecimal getTotalPolicyPremiumValue() {
        return totalPolicyPremiumValue;
    }

    public BigDecimal getActivePolicyPremiumValue() {
        return activePolicyPremiumValue;
    }

    public BigDecimal getCancelledPolicyPremiumValue() {
        return cancelledPolicyPremiumValue;
    }

    public BigDecimal getCompletedPaymentValue() {
        return completedPaymentValue;
    }

    public BigDecimal getPendingPaymentValue() {
        return pendingPaymentValue;
    }

    public BigDecimal getProcessingPaymentValue() {
        return processingPaymentValue;
    }

    public BigDecimal getOutstandingPaymentValue() {
        return outstandingPaymentValue;
    }

    public BigDecimal getFailedPaymentValue() {
        return failedPaymentValue;
    }

    public long getTotalCompletedPayments() {
        return totalCompletedPayments;
    }

    public long getTotalPendingPayments() {
        return totalPendingPayments;
    }

    public long getTotalProcessingPayments() {
        return totalProcessingPayments;
    }

    public long getTotalFailedPayments() {
        return totalFailedPayments;
    }

    public long getTotalInquiries() {
        return totalInquiries;
    }

    public long getConvertedInquiries() {
        return convertedInquiries;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }
}