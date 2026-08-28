package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.reporting.AnalyticsSummaryResponse;
import ke.co.jodam.insurance.dto.reporting.InquiryReportResponse;
import ke.co.jodam.insurance.dto.reporting.PaymentReportResponse;
import ke.co.jodam.insurance.dto.reporting.PerformanceReportResponse;
import ke.co.jodam.insurance.dto.reporting.PolicyReportResponse;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportingExportService {

    private final ReportingService reportingService;

    public ReportingExportService(
            ReportingService reportingService
    ) {
        this.reportingService = reportingService;
    }

    public byte[] exportPoliciesCsv() {

        List<PolicyReportResponse> policies =
                reportingService.getPolicyReport();

        StringBuilder csv = new StringBuilder();

        appendRow(
                csv,
                "ID",
                "Policy Number",
                "Inquiry ID",
                "Quotation ID",
                "Customer",
                "Agent",
                "Insurance Type",
                "Premium Amount",
                "Coverage Details",
                "Start Date",
                "End Date",
                "Status",
                "Created At",
                "Updated At"
        );

        for (PolicyReportResponse policy : policies) {
            appendRow(
                    csv,
                    policy.getId(),
                    policy.getPolicyNumber(),
                    policy.getInquiryId(),
                    policy.getQuotationId(),
                    policy.getCustomerUsername(),
                    policy.getAgentUsername(),
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

        return toBytes(csv);
    }

    public byte[] exportPaymentsCsv() {

        List<PaymentReportResponse> payments =
                reportingService.getPaymentReport();

        StringBuilder csv = new StringBuilder();

        appendRow(
                csv,
                "ID",
                "Policy ID",
                "Policy Number",
                "Customer",
                "Agent",
                "Amount",
                "Phone Number",
                "Transaction Reference",
                "Checkout Request ID",
                "Status",
                "Created At",
                "Updated At"
        );

        for (PaymentReportResponse payment : payments) {
            appendRow(
                    csv,
                    payment.getId(),
                    payment.getPolicyId(),
                    payment.getPolicyNumber(),
                    payment.getCustomerUsername(),
                    payment.getAgentUsername(),
                    payment.getAmount(),
                    payment.getPhoneNumber(),
                    payment.getTransactionReference(),
                    payment.getCheckoutRequestId(),
                    payment.getStatus(),
                    payment.getCreatedAt(),
                    payment.getUpdatedAt()
            );
        }

        return toBytes(csv);
    }

    public byte[] exportInquiriesCsv() {

        List<InquiryReportResponse> inquiries =
                reportingService.getInquiryReport();

        StringBuilder csv = new StringBuilder();

        appendRow(
                csv,
                "ID",
                "Insurance Type",
                "Description",
                "Status",
                "Customer",
                "Assigned Agent",
                "Created At",
                "Updated At"
        );

        for (InquiryReportResponse inquiry : inquiries) {
            appendRow(
                    csv,
                    inquiry.getId(),
                    inquiry.getInsuranceType(),
                    inquiry.getDescription(),
                    inquiry.getStatus(),
                    inquiry.getCustomerUsername(),
                    inquiry.getAssignedAgentUsername(),
                    inquiry.getCreatedAt(),
                    inquiry.getUpdatedAt()
            );
        }

        return toBytes(csv);
    }

    public byte[] exportAnalyticsCsv() {

        AnalyticsSummaryResponse analytics =
                reportingService.getAnalyticsSummary();

        StringBuilder csv = new StringBuilder();

        appendRow(
                csv,
                "Metric",
                "Value"
        );

        appendRow(
                csv,
                "Total Policy Premium Value",
                analytics.getTotalPolicyPremiumValue()
        );

        appendRow(
                csv,
                "Active Policy Premium Value",
                analytics.getActivePolicyPremiumValue()
        );

        appendRow(
                csv,
                "Cancelled Policy Premium Value",
                analytics.getCancelledPolicyPremiumValue()
        );

        appendRow(
                csv,
                "Completed Payment Value",
                analytics.getCompletedPaymentValue()
        );

        appendRow(
                csv,
                "Pending Payment Value",
                analytics.getPendingPaymentValue()
        );

        appendRow(
                csv,
                "Processing Payment Value",
                analytics.getProcessingPaymentValue()
        );

        appendRow(
                csv,
                "Outstanding Payment Value",
                analytics.getOutstandingPaymentValue()
        );

        appendRow(
                csv,
                "Failed Payment Value",
                analytics.getFailedPaymentValue()
        );

        appendRow(
                csv,
                "Completed Payment Count",
                analytics.getTotalCompletedPayments()
        );

        appendRow(
                csv,
                "Pending Payment Count",
                analytics.getTotalPendingPayments()
        );

        appendRow(
                csv,
                "Processing Payment Count",
                analytics.getTotalProcessingPayments()
        );

        appendRow(
                csv,
                "Failed Payment Count",
                analytics.getTotalFailedPayments()
        );

        appendRow(
                csv,
                "Total Inquiries",
                analytics.getTotalInquiries()
        );

        appendRow(
                csv,
                "Converted Inquiries",
                analytics.getConvertedInquiries()
        );

        appendRow(
                csv,
                "Conversion Rate",
                analytics.getConversionRate()
        );

        return toBytes(csv);
    }

    public byte[] exportMonthlyPerformanceCsv() {

        List<PerformanceReportResponse> performance =
                reportingService.getMonthlyPerformanceReport();

        StringBuilder csv = new StringBuilder();

        appendPerformanceHeader(csv);

        for (PerformanceReportResponse report : performance) {
            appendPerformanceRow(csv, report);
        }

        return toBytes(csv);
    }

    public byte[] exportAnnualPerformanceCsv() {

        List<PerformanceReportResponse> performance =
                reportingService.getAnnualPerformanceReport();

        StringBuilder csv = new StringBuilder();

        appendPerformanceHeader(csv);

        for (PerformanceReportResponse report : performance) {
            appendPerformanceRow(csv, report);
        }

        return toBytes(csv);
    }

    private void appendPerformanceHeader(
            StringBuilder csv
    ) {
        appendRow(
                csv,
                "Period",
                "Policy Count",
                "Premium Value",
                "Completed Payment Count",
                "Completed Payment Value",
                "Inquiry Count",
                "Converted Inquiry Count",
                "Conversion Rate"
        );
    }

    private void appendPerformanceRow(
            StringBuilder csv,
            PerformanceReportResponse report
    ) {
        appendRow(
                csv,
                report.getPeriod(),
                report.getPolicyCount(),
                report.getPremiumValue(),
                report.getCompletedPaymentCount(),
                report.getCompletedPaymentValue(),
                report.getInquiryCount(),
                report.getConvertedInquiryCount(),
                report.getConversionRate()
        );
    }

    private void appendRow(
            StringBuilder csv,
            Object... values
    ) {
        for (int i = 0; i < values.length; i++) {

            if (i > 0) {
                csv.append(',');
            }

            csv.append(
                    escapeCsvValue(
                            values[i]
                    )
            );
        }

        csv.append('\n');
    }

    private String escapeCsvValue(
            Object value
    ) {
        if (value == null) {
            return "";
        }

        String text = String.valueOf(value);

        boolean requiresQuotes =
                text.contains(",")
                        || text.contains("\"")
                        || text.contains("\r")
                        || text.contains("\n");

        if (!requiresQuotes) {
            return text;
        }

        return "\""
                + text.replace("\"", "\"\"")
                + "\"";
    }

    private byte[] toBytes(
            StringBuilder csv
    ) {
        return csv
                .toString()
                .getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                );
    }
}