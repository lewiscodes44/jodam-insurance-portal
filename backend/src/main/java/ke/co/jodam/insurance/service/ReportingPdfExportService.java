package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.reporting.AnalyticsSummaryResponse;
import ke.co.jodam.insurance.dto.reporting.DashboardSummaryResponse;
import ke.co.jodam.insurance.dto.reporting.InquiryReportResponse;
import ke.co.jodam.insurance.dto.reporting.PaymentReportResponse;
import ke.co.jodam.insurance.dto.reporting.PerformanceReportResponse;
import ke.co.jodam.insurance.dto.reporting.PolicyReportResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportingPdfExportService {

    private static final float PAGE_MARGIN = 36f;
    private static final float TITLE_SIZE = 18f;
    private static final float SECTION_SIZE = 12f;
    private static final float BODY_SIZE = 8.5f;
    private static final float SMALL_SIZE = 7.5f;
    private static final float LINE_HEIGHT = 11f;
    private static final float SECTION_GAP = 8f;

    private static final PDType1Font REGULAR_FONT =
            new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA
            );

    private static final PDType1Font BOLD_FONT =
            new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA_BOLD
            );

    private final ReportingService reportingService;

    public ReportingPdfExportService(
            ReportingService reportingService
    ) {
        this.reportingService = reportingService;
    }

    public byte[] exportReport() {

        try (
                PDDocument document = new PDDocument();
                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {
            PdfWriter writer =
                    new PdfWriter(document);

            writeTitlePage(writer);
            writeDashboardSection(writer);
            writeAnalyticsSection(writer);
            writePolicySection(writer);
            writePaymentSection(writer);
            writeInquirySection(writer);
            writeMonthlyPerformanceSection(writer);
            writeAnnualPerformanceSection(writer);

            writer.close();

            document.save(outputStream);

            return outputStream.toByteArray();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to generate PDF reporting document.",
                    exception
            );
        }
    }

    private void writeTitlePage(
            PdfWriter writer
    ) throws IOException {

        writer.addTitle(
                "Jodam Insurance Portal - Reporting Summary"
        );

        writer.writeTitle(
                "Jodam Insurance Portal"
        );

        writer.writeSubtitle(
                "Reporting Summary"
        );

        writer.writeText(
                "Generated: "
                        + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd HH:mm:ss"
                        )
                ),
                BODY_SIZE
        );

        writer.writeText(
                "This report contains the current administrative "
                        + "reporting summary, analytics, operational "
                        + "reports, and performance information.",
                BODY_SIZE
        );

        writer.newLine(12f);
    }

    private void writeDashboardSection(
            PdfWriter writer
    ) throws IOException {

        DashboardSummaryResponse dashboard =
                reportingService.getDashboardSummary();

        writer.sectionHeading(
                "1. Dashboard Summary"
        );

        writer.tableHeader(
                "Metric",
                "Value"
        );

        writer.metric(
                "Total Inquiries",
                dashboard.getTotalInquiries()
        );

        writer.metric(
                "New Inquiries",
                dashboard.getNewInquiries()
        );

        writer.metric(
                "Assigned Inquiries",
                dashboard.getAssignedInquiries()
        );

        writer.metric(
                "Quoted Inquiries",
                dashboard.getQuotedInquiries()
        );

        writer.metric(
                "Accepted Inquiries",
                dashboard.getAcceptedInquiries()
        );

        writer.metric(
                "Rejected Inquiries",
                dashboard.getRejectedInquiries()
        );

        writer.metric(
                "Converted Inquiries",
                dashboard.getConvertedInquiries()
        );

        writer.metric(
                "Total Policies",
                dashboard.getTotalPolicies()
        );

        writer.metric(
                "Pending Payment Policies",
                dashboard.getPendingPaymentPolicies()
        );

        writer.metric(
                "Active Policies",
                dashboard.getActivePolicies()
        );

        writer.metric(
                "Expired Policies",
                dashboard.getExpiredPolicies()
        );

        writer.metric(
                "Cancelled Policies",
                dashboard.getCancelledPolicies()
        );

        writer.metric(
                "Total Payments",
                dashboard.getTotalPayments()
        );

        writer.metric(
                "Pending Payments",
                dashboard.getPendingPayments()
        );

        writer.metric(
                "Processing Payments",
                dashboard.getProcessingPayments()
        );

        writer.metric(
                "Completed Payments",
                dashboard.getCompletedPayments()
        );

        writer.metric(
                "Failed Payments",
                dashboard.getFailedPayments()
        );

        writer.metric(
                "Cancelled Payments",
                dashboard.getCancelledPayments()
        );

        writer.metric(
                "Total Completed Payment Amount",
                dashboard.getTotalCompletedPaymentAmount()
        );
    }

    private void writeAnalyticsSection(
            PdfWriter writer
    ) throws IOException {

        AnalyticsSummaryResponse analytics =
                reportingService.getAnalyticsSummary();

        writer.sectionHeading(
                "2. Analytics Summary"
        );

        writer.tableHeader(
                "Metric",
                "Value"
        );

        writer.metric(
                "Total Policy Premium Value",
                analytics.getTotalPolicyPremiumValue()
        );

        writer.metric(
                "Active Policy Premium Value",
                analytics.getActivePolicyPremiumValue()
        );

        writer.metric(
                "Cancelled Policy Premium Value",
                analytics.getCancelledPolicyPremiumValue()
        );

        writer.metric(
                "Completed Payment Value",
                analytics.getCompletedPaymentValue()
        );

        writer.metric(
                "Pending Payment Value",
                analytics.getPendingPaymentValue()
        );

        writer.metric(
                "Processing Payment Value",
                analytics.getProcessingPaymentValue()
        );

        writer.metric(
                "Outstanding Payment Value",
                analytics.getOutstandingPaymentValue()
        );

        writer.metric(
                "Failed Payment Value",
                analytics.getFailedPaymentValue()
        );

        writer.metric(
                "Completed Payment Count",
                analytics.getTotalCompletedPayments()
        );

        writer.metric(
                "Pending Payment Count",
                analytics.getTotalPendingPayments()
        );

        writer.metric(
                "Processing Payment Count",
                analytics.getTotalProcessingPayments()
        );

        writer.metric(
                "Failed Payment Count",
                analytics.getTotalFailedPayments()
        );

        writer.metric(
                "Total Inquiries",
                analytics.getTotalInquiries()
        );

        writer.metric(
                "Converted Inquiries",
                analytics.getConvertedInquiries()
        );

        writer.metric(
                "Conversion Rate",
                analytics.getConversionRate()
        );
    }

    private void writePolicySection(
            PdfWriter writer
    ) throws IOException {

        List<PolicyReportResponse> policies =
                reportingService.getPolicyReport();

        writer.sectionHeading(
                "3. Policy Report"
        );

        writer.policyHeader();

        for (PolicyReportResponse policy : policies) {
            writer.policyRow(policy);
        }
    }

    private void writePaymentSection(
            PdfWriter writer
    ) throws IOException {

        List<PaymentReportResponse> payments =
                reportingService.getPaymentReport();

        writer.sectionHeading(
                "4. Payment Report"
        );

        writer.paymentHeader();

        for (PaymentReportResponse payment : payments) {
            writer.paymentRow(payment);
        }
    }

    private void writeInquirySection(
            PdfWriter writer
    ) throws IOException {

        List<InquiryReportResponse> inquiries =
                reportingService.getInquiryReport();

        writer.sectionHeading(
                "5. Inquiry Report"
        );

        writer.inquiryHeader();

        for (InquiryReportResponse inquiry : inquiries) {
            writer.inquiryRow(inquiry);
        }
    }

    private void writeMonthlyPerformanceSection(
            PdfWriter writer
    ) throws IOException {

        List<PerformanceReportResponse> performance =
                reportingService.getMonthlyPerformanceReport();

        writer.sectionHeading(
                "6. Monthly Performance"
        );

        writer.performanceHeader();

        for (PerformanceReportResponse report : performance) {
            writer.performanceRow(report);
        }
    }

    private void writeAnnualPerformanceSection(
            PdfWriter writer
    ) throws IOException {

        List<PerformanceReportResponse> performance =
                reportingService.getAnnualPerformanceReport();

        writer.sectionHeading(
                "7. Annual Performance"
        );

        writer.performanceHeader();

        for (PerformanceReportResponse report : performance) {
            writer.performanceRow(report);
        }
    }

    private static class PdfWriter {

        private final PDDocument document;

        private PDPage page;
        private PDPageContentStream contentStream;

        private float y;
        private final float pageWidth;
        private final float pageHeight;

        private PdfWriter(
                PDDocument document
        ) throws IOException {

            this.document = document;

            PDRectangle rectangle =
                    PDRectangle.A4;

            this.pageWidth =
                    rectangle.getWidth();

            this.pageHeight =
                    rectangle.getHeight();

            addPage();
        }

        private void addPage()
                throws IOException {

            if (contentStream != null) {
                contentStream.close();
            }

            page =
                    new PDPage(
                            PDRectangle.A4
                    );

            document.addPage(page);

            contentStream =
                    new PDPageContentStream(
                            document,
                            page
                    );

            y =
                    pageHeight - PAGE_MARGIN;

            writeFooter();
        }

        private void writeFooter()
                throws IOException {

            contentStream.beginText();

            contentStream.setFont(
                    REGULAR_FONT,
                    7f
            );

            contentStream.newLineAtOffset(
                    PAGE_MARGIN,
                    18f
            );

            contentStream.showText(
                    "Jodam Insurance Portal - Administrative Report"
            );

            contentStream.endText();
        }

        private void ensureSpace(
                float requiredHeight
        ) throws IOException {

            if (y - requiredHeight
                    < PAGE_MARGIN + 25f) {

                addPage();
            }
        }

        private void writeTitle(
                String text
        ) throws IOException {

            ensureSpace(30f);

            writeLine(
                    text,
                    BOLD_FONT,
                    TITLE_SIZE
            );

            newLine(6f);
        }

        private void writeSubtitle(
                String text
        ) throws IOException {

            ensureSpace(22f);

            writeLine(
                    text,
                    BOLD_FONT,
                    13f
            );

            newLine(8f);
        }

        private void writeText(
                String text,
                float fontSize
        ) throws IOException {

            List<String> lines =
                    wrapText(
                            text,
                            fontSize,
                            pageWidth
                                    - (PAGE_MARGIN * 2)
                    );

            for (String line : lines) {

                ensureSpace(
                        LINE_HEIGHT
                );

                writeLine(
                        line,
                        REGULAR_FONT,
                        fontSize
                );
            }

            newLine(4f);
        }

        private void sectionHeading(
                String text
        ) throws IOException {

            ensureSpace(28f);

            newLine(
                    SECTION_GAP
            );

            writeLine(
                    text,
                    BOLD_FONT,
                    SECTION_SIZE
            );

            newLine(5f);
        }

        private void tableHeader(
                String left,
                String right
        ) throws IOException {

            ensureSpace(22f);

            writeColumns(
                    left,
                    right,
                    BOLD_FONT,
                    BODY_SIZE,
                    210f
            );
        }

        private void metric(
                String metric,
                Object value
        ) throws IOException {

            ensureSpace(16f);

            writeColumns(
                    metric,
                    String.valueOf(value),
                    REGULAR_FONT,
                    BODY_SIZE,
                    210f
            );
        }

        private void policyHeader()
                throws IOException {

            ensureSpace(28f);

            writeLine(
                    "Policy Number | Customer | Agent | "
                            + "Type | Premium | Start | End | Status",
                    BOLD_FONT,
                    SMALL_SIZE
            );

            newLine(2f);

            writeLine(
                    "Coverage Details",
                    BOLD_FONT,
                    SMALL_SIZE
            );

            newLine(3f);
        }

        private void policyRow(
                PolicyReportResponse policy
        ) throws IOException {

            String firstLine =
                    safe(policy.getPolicyNumber())
                            + " | "
                            + safe(
                            policy.getCustomerUsername()
                    )
                            + " | "
                            + safe(
                            policy.getAgentUsername()
                    )
                            + " | "
                            + safe(
                            policy.getInsuranceType()
                    )
                            + " | "
                            + safe(
                            policy.getPremiumAmount()
                    )
                            + " | "
                            + safe(
                            policy.getStartDate()
                    )
                            + " | "
                            + safe(
                            policy.getEndDate()
                    )
                            + " | "
                            + safe(
                            policy.getStatus()
                    );

            writeText(
                    firstLine,
                    SMALL_SIZE
            );

            writeText(
                    "Coverage: "
                            + safe(
                            policy.getCoverageDetails()
                    ),
                    SMALL_SIZE
            );

            writeText(
                    "Created: "
                            + safe(
                            policy.getCreatedAt()
                    )
                            + " | Updated: "
                            + safe(
                            policy.getUpdatedAt()
                    ),
                    SMALL_SIZE
            );
        }

        private void paymentHeader()
                throws IOException {

            ensureSpace(22f);

            writeLine(
                    "ID | Policy | Customer | Agent | Amount | "
                            + "Status | Transaction Reference",
                    BOLD_FONT,
                    SMALL_SIZE
            );

            newLine(3f);
        }

        private void paymentRow(
                PaymentReportResponse payment
        ) throws IOException {

            String line =
                    safe(payment.getId())
                            + " | "
                            + safe(
                            payment.getPolicyNumber()
                    )
                            + " | "
                            + safe(
                            payment.getCustomerUsername()
                    )
                            + " | "
                            + safe(
                            payment.getAgentUsername()
                    )
                            + " | "
                            + safe(payment.getAmount())
                            + " | "
                            + safe(payment.getStatus())
                            + " | "
                            + safe(
                            payment.getTransactionReference()
                    );

            writeText(
                    line,
                    SMALL_SIZE
            );

            writeText(
                    "Phone: "
                            + safe(
                            payment.getPhoneNumber()
                    )
                            + " | Checkout: "
                            + safe(
                            payment.getCheckoutRequestId()
                    ),
                    SMALL_SIZE
            );

            writeText(
                    "Created: "
                            + safe(
                            payment.getCreatedAt()
                    )
                            + " | Updated: "
                            + safe(
                            payment.getUpdatedAt()
                    ),
                    SMALL_SIZE
            );
        }

        private void inquiryHeader()
                throws IOException {

            ensureSpace(22f);

            writeLine(
                    "ID | Type | Status | Customer | Assigned Agent",
                    BOLD_FONT,
                    SMALL_SIZE
            );

            newLine(3f);
        }

        private void inquiryRow(
                InquiryReportResponse inquiry
        ) throws IOException {

            String line =
                    safe(inquiry.getId())
                            + " | "
                            + safe(
                            inquiry.getInsuranceType()
                    )
                            + " | "
                            + safe(inquiry.getStatus())
                            + " | "
                            + safe(
                            inquiry.getCustomerUsername()
                    )
                            + " | "
                            + safe(
                            inquiry.getAssignedAgentUsername()
                    );

            writeText(
                    line,
                    SMALL_SIZE
            );

            writeText(
                    "Description: "
                            + safe(
                            inquiry.getDescription()
                    ),
                    SMALL_SIZE
            );

            writeText(
                    "Created: "
                            + safe(
                            inquiry.getCreatedAt()
                    )
                            + " | Updated: "
                            + safe(
                            inquiry.getUpdatedAt()
                    ),
                    SMALL_SIZE
            );
        }

        private void performanceHeader()
                throws IOException {

            ensureSpace(22f);

            writeLine(
                    "Period | Policies | Premium | Completed "
                            + "Payments | Completed Value | Inquiries | "
                            + "Converted | Conversion Rate",
                    BOLD_FONT,
                    SMALL_SIZE
            );

            newLine(3f);
        }

        private void performanceRow(
                PerformanceReportResponse report
        ) throws IOException {

            String line =
                    safe(report.getPeriod())
                            + " | "
                            + safe(report.getPolicyCount())
                            + " | "
                            + safe(report.getPremiumValue())
                            + " | "
                            + safe(
                            report.getCompletedPaymentCount()
                    )
                            + " | "
                            + safe(
                            report.getCompletedPaymentValue()
                    )
                            + " | "
                            + safe(report.getInquiryCount())
                            + " | "
                            + safe(
                            report.getConvertedInquiryCount()
                    )
                            + " | "
                            + safe(
                            report.getConversionRate()
                    );

            writeText(
                    line,
                    SMALL_SIZE
            );
        }

        private void writeColumns(
                String left,
                String right,
                PDType1Font font,
                float fontSize,
                float leftWidth
        ) throws IOException {

            ensureSpace(
                    LINE_HEIGHT
            );

            contentStream.beginText();

            contentStream.setFont(
                    font,
                    fontSize
            );

            contentStream.newLineAtOffset(
                    PAGE_MARGIN,
                    y
            );

            contentStream.showText(
                    fitText(
                            safe(left),
                            font,
                            fontSize,
                            leftWidth
                    )
            );

            contentStream.newLineAtOffset(
                    leftWidth,
                    0
            );

            contentStream.showText(
                    fitText(
                            safe(right),
                            font,
                            fontSize,
                            pageWidth
                                    - PAGE_MARGIN
                                    - leftWidth
                                    - PAGE_MARGIN
                    )
            );

            contentStream.endText();

            y -= LINE_HEIGHT;
        }

        private void writeLine(
                String text,
                PDType1Font font,
                float fontSize
        ) throws IOException {

            contentStream.beginText();

            contentStream.setFont(
                    font,
                    fontSize
            );

            contentStream.newLineAtOffset(
                    PAGE_MARGIN,
                    y
            );

            contentStream.showText(
                    sanitizePdfText(text)
            );

            contentStream.endText();

            y -= LINE_HEIGHT;
        }

        private void newLine(
                float amount
        ) {
            y -= amount;
        }

        private String fitText(
                String text,
                PDType1Font font,
                float fontSize,
                float maxWidth
        ) throws IOException {

            String sanitized =
                    sanitizePdfText(text);

            if (font.getStringWidth(
                    sanitized
            ) / 1000f * fontSize <= maxWidth) {
                return sanitized;
            }

            StringBuilder builder =
                    new StringBuilder();

            for (char character : sanitized.toCharArray()) {

                String candidate =
                        builder
                                .toString()
                                + character;

                float width =
                        font.getStringWidth(
                                candidate
                        )
                                / 1000f
                                * fontSize;

                if (width > maxWidth) {
                    break;
                }

                builder.append(
                        character
                );
            }

            return builder
                    .append("...")
                    .toString();
        }

        private List<String> wrapText(
                String text,
                float fontSize,
                float maxWidth
        ) throws IOException {

            String sanitized =
                    sanitizePdfText(text);

            String[] words =
                    sanitized.split("\\s+");

            List<String> lines =
                    new java.util.ArrayList<>();

            StringBuilder current =
                    new StringBuilder();

            for (String word : words) {

                String candidate =
                        current.length() == 0
                                ? word
                                : current
                                + " "
                                + word;

                float width =
                        REGULAR_FONT.getStringWidth(
                                candidate
                        )
                                / 1000f
                                * fontSize;

                if (width <= maxWidth) {
                    current =
                            new StringBuilder(
                                    candidate
                            );
                } else {

                    if (current.length() > 0) {
                        lines.add(
                                current.toString()
                        );
                    }

                    current =
                            new StringBuilder(
                                    word
                            );
                }
            }

            if (current.length() > 0) {
                lines.add(
                        current.toString()
                );
            }

            if (lines.isEmpty()) {
                lines.add("");
            }

            return lines;
        }

        private String sanitizePdfText(
                String text
        ) {
            if (text == null) {
                return "";
            }

            return text
                    .replace(
                            "\r",
                            " "
                    )
                    .replace(
                            "\n",
                            " "
                    )
                    .replace(
                            "\t",
                            " "
                    )
                    .replace(
                            "\u2018",
                            "'"
                    )
                    .replace(
                            "\u2019",
                            "'"
                    )
                    .replace(
                            "\u201C",
                            "\""
                    )
                    .replace(
                            "\u201D",
                            "\""
                    )
                    .replace(
                            "\u2013",
                            "-"
                    )
                    .replace(
                            "\u2014",
                            "-"
                    );
        }

        private String safe(
                Object value
        ) {
            return value == null
                    ? ""
                    : String.valueOf(value);
        }

        private void close()
                throws IOException {

            if (contentStream != null) {
                contentStream.close();
                contentStream = null;
            }
        }

        private void addTitle(
                String title
        ) {
            page.setMediaBox(
                    PDRectangle.A4
            );
        }
    }
}