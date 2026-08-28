package ke.co.jodam.insurance.dto.reporting;

import java.math.BigDecimal;

public class PerformanceReportResponse {

    private String period;

    private long policyCount;
    private BigDecimal premiumValue;

    private long completedPaymentCount;
    private BigDecimal completedPaymentValue;

    private long inquiryCount;
    private long convertedInquiryCount;
    private BigDecimal conversionRate;

    public PerformanceReportResponse(
            String period,
            long policyCount,
            BigDecimal premiumValue,
            long completedPaymentCount,
            BigDecimal completedPaymentValue,
            long inquiryCount,
            long convertedInquiryCount,
            BigDecimal conversionRate
    ) {
        this.period = period;
        this.policyCount = policyCount;
        this.premiumValue = premiumValue;
        this.completedPaymentCount = completedPaymentCount;
        this.completedPaymentValue = completedPaymentValue;
        this.inquiryCount = inquiryCount;
        this.convertedInquiryCount = convertedInquiryCount;
        this.conversionRate = conversionRate;
    }

    public String getPeriod() {
        return period;
    }

    public long getPolicyCount() {
        return policyCount;
    }

    public BigDecimal getPremiumValue() {
        return premiumValue;
    }

    public long getCompletedPaymentCount() {
        return completedPaymentCount;
    }

    public BigDecimal getCompletedPaymentValue() {
        return completedPaymentValue;
    }

    public long getInquiryCount() {
        return inquiryCount;
    }

    public long getConvertedInquiryCount() {
        return convertedInquiryCount;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }
}