package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.reporting.AnalyticsSummaryResponse;
import ke.co.jodam.insurance.dto.reporting.InquiryReportResponse;
import ke.co.jodam.insurance.dto.reporting.PaymentReportResponse;
import ke.co.jodam.insurance.dto.reporting.PerformanceReportResponse;
import ke.co.jodam.insurance.dto.reporting.PolicyReportResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportingExcelExportService {

    private final ReportingService reportingService;

    public ReportingExcelExportService(
            ReportingService reportingService
    ) {
        this.reportingService = reportingService;
    }

    public byte[] exportWorkbook() {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {

            CellStyle headerStyle =
                    createHeaderStyle(workbook);

            createPoliciesSheet(
                    workbook,
                    headerStyle
            );

            createPaymentsSheet(
                    workbook,
                    headerStyle
            );

            createInquiriesSheet(
                    workbook,
                    headerStyle
            );

            createAnalyticsSheet(
                    workbook,
                    headerStyle
            );

            createPerformanceSheet(
                    workbook,
                    headerStyle,
                    "Monthly Performance",
                    reportingService.getMonthlyPerformanceReport()
            );

            createPerformanceSheet(
                    workbook,
                    headerStyle,
                    "Annual Performance",
                    reportingService.getAnnualPerformanceReport()
            );

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to generate Excel reporting workbook.",
                    exception
            );
        }
    }

    private void createPoliciesSheet(
            Workbook workbook,
            CellStyle headerStyle
    ) {
        Sheet sheet =
                workbook.createSheet("Policies");

        String[] headers = {
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
        };

        int rowIndex =
                createHeaderRow(
                        sheet,
                        headerStyle,
                        headers
                );

        List<PolicyReportResponse> policies =
                reportingService.getPolicyReport();

        for (PolicyReportResponse policy : policies) {

            Row row =
                    sheet.createRow(rowIndex++);

            setCell(row, 0, policy.getId());
            setCell(row, 1, policy.getPolicyNumber());
            setCell(row, 2, policy.getInquiryId());
            setCell(row, 3, policy.getQuotationId());
            setCell(row, 4, policy.getCustomerUsername());
            setCell(row, 5, policy.getAgentUsername());
            setCell(row, 6, policy.getInsuranceType());
            setCell(row, 7, policy.getPremiumAmount());
            setCell(row, 8, policy.getCoverageDetails());
            setCell(row, 9, policy.getStartDate());
            setCell(row, 10, policy.getEndDate());
            setCell(row, 11, policy.getStatus());
            setCell(row, 12, policy.getCreatedAt());
            setCell(row, 13, policy.getUpdatedAt());
        }

        autoSizeColumns(
                sheet,
                headers.length
        );
    }

    private void createPaymentsSheet(
            Workbook workbook,
            CellStyle headerStyle
    ) {
        Sheet sheet =
                workbook.createSheet("Payments");

        String[] headers = {
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
        };

        int rowIndex =
                createHeaderRow(
                        sheet,
                        headerStyle,
                        headers
                );

        List<PaymentReportResponse> payments =
                reportingService.getPaymentReport();

        for (PaymentReportResponse payment : payments) {

            Row row =
                    sheet.createRow(rowIndex++);

            setCell(row, 0, payment.getId());
            setCell(row, 1, payment.getPolicyId());
            setCell(row, 2, payment.getPolicyNumber());
            setCell(row, 3, payment.getCustomerUsername());
            setCell(row, 4, payment.getAgentUsername());
            setCell(row, 5, payment.getAmount());
            setCell(row, 6, payment.getPhoneNumber());
            setCell(row, 7, payment.getTransactionReference());
            setCell(row, 8, payment.getCheckoutRequestId());
            setCell(row, 9, payment.getStatus());
            setCell(row, 10, payment.getCreatedAt());
            setCell(row, 11, payment.getUpdatedAt());
        }

        autoSizeColumns(
                sheet,
                headers.length
        );
    }

    private void createInquiriesSheet(
            Workbook workbook,
            CellStyle headerStyle
    ) {
        Sheet sheet =
                workbook.createSheet("Inquiries");

        String[] headers = {
                "ID",
                "Insurance Type",
                "Description",
                "Status",
                "Customer",
                "Assigned Agent",
                "Created At",
                "Updated At"
        };

        int rowIndex =
                createHeaderRow(
                        sheet,
                        headerStyle,
                        headers
                );

        List<InquiryReportResponse> inquiries =
                reportingService.getInquiryReport();

        for (InquiryReportResponse inquiry : inquiries) {

            Row row =
                    sheet.createRow(rowIndex++);

            setCell(row, 0, inquiry.getId());
            setCell(row, 1, inquiry.getInsuranceType());
            setCell(row, 2, inquiry.getDescription());
            setCell(row, 3, inquiry.getStatus());
            setCell(row, 4, inquiry.getCustomerUsername());
            setCell(row, 5, inquiry.getAssignedAgentUsername());
            setCell(row, 6, inquiry.getCreatedAt());
            setCell(row, 7, inquiry.getUpdatedAt());
        }

        autoSizeColumns(
                sheet,
                headers.length
        );
    }

    private void createAnalyticsSheet(
            Workbook workbook,
            CellStyle headerStyle
    ) {
        Sheet sheet =
                workbook.createSheet("Analytics");

        String[] headers = {
                "Metric",
                "Value"
        };

        int rowIndex =
                createHeaderRow(
                        sheet,
                        headerStyle,
                        headers
                );

        AnalyticsSummaryResponse analytics =
                reportingService.getAnalyticsSummary();

        addMetricRow(
                sheet,
                rowIndex++,
                "Total Policy Premium Value",
                analytics.getTotalPolicyPremiumValue()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Active Policy Premium Value",
                analytics.getActivePolicyPremiumValue()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Cancelled Policy Premium Value",
                analytics.getCancelledPolicyPremiumValue()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Completed Payment Value",
                analytics.getCompletedPaymentValue()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Pending Payment Value",
                analytics.getPendingPaymentValue()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Processing Payment Value",
                analytics.getProcessingPaymentValue()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Outstanding Payment Value",
                analytics.getOutstandingPaymentValue()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Failed Payment Value",
                analytics.getFailedPaymentValue()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Completed Payment Count",
                analytics.getTotalCompletedPayments()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Pending Payment Count",
                analytics.getTotalPendingPayments()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Processing Payment Count",
                analytics.getTotalProcessingPayments()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Failed Payment Count",
                analytics.getTotalFailedPayments()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Total Inquiries",
                analytics.getTotalInquiries()
        );

        addMetricRow(
                sheet,
                rowIndex++,
                "Converted Inquiries",
                analytics.getConvertedInquiries()
        );

        addMetricRow(
                sheet,
                rowIndex,
                "Conversion Rate",
                analytics.getConversionRate()
        );

        autoSizeColumns(
                sheet,
                headers.length
        );
    }

    private void createPerformanceSheet(
            Workbook workbook,
            CellStyle headerStyle,
            String sheetName,
            List<PerformanceReportResponse> reports
    ) {
        Sheet sheet =
                workbook.createSheet(sheetName);

        String[] headers = {
                "Period",
                "Policy Count",
                "Premium Value",
                "Completed Payment Count",
                "Completed Payment Value",
                "Inquiry Count",
                "Converted Inquiry Count",
                "Conversion Rate"
        };

        int rowIndex =
                createHeaderRow(
                        sheet,
                        headerStyle,
                        headers
                );

        for (PerformanceReportResponse report : reports) {

            Row row =
                    sheet.createRow(rowIndex++);

            setCell(row, 0, report.getPeriod());
            setCell(row, 1, report.getPolicyCount());
            setCell(row, 2, report.getPremiumValue());
            setCell(
                    row,
                    3,
                    report.getCompletedPaymentCount()
            );
            setCell(
                    row,
                    4,
                    report.getCompletedPaymentValue()
            );
            setCell(
                    row,
                    5,
                    report.getInquiryCount()
            );
            setCell(
                    row,
                    6,
                    report.getConvertedInquiryCount()
            );
            setCell(
                    row,
                    7,
                    report.getConversionRate()
            );
        }

        autoSizeColumns(
                sheet,
                headers.length
        );
    }

    private int createHeaderRow(
            Sheet sheet,
            CellStyle headerStyle,
            String[] headers
    ) {
        Row row =
                sheet.createRow(0);

        for (int index = 0;
             index < headers.length;
             index++) {

            Cell cell =
                    row.createCell(index);

            cell.setCellValue(
                    headers[index]
            );

            cell.setCellStyle(
                    headerStyle
            );
        }

        sheet.createFreezePane(
                0,
                1
        );

        return 1;
    }

    private CellStyle createHeaderStyle(
            Workbook workbook
    ) {
        CellStyle style =
                workbook.createCellStyle();

        Font font =
                workbook.createFont();

        font.setBold(true);

        style.setFont(font);

        return style;
    }

    private void addMetricRow(
            Sheet sheet,
            int rowIndex,
            String metric,
            Object value
    ) {
        Row row =
                sheet.createRow(rowIndex);

        setCell(row, 0, metric);
        setCell(row, 1, value);
    }

    private void setCell(
            Row row,
            int columnIndex,
            Object value
    ) {
        Cell cell =
                row.createCell(columnIndex);

        if (value == null) {
            return;
        }

        if (value instanceof BigDecimal decimal) {
            cell.setCellValue(
                    decimal.doubleValue()
            );
            return;
        }

        if (value instanceof Number number) {
            cell.setCellValue(
                    number.doubleValue()
            );
            return;
        }

        if (value instanceof Boolean booleanValue) {
            cell.setCellValue(
                    booleanValue
            );
            return;
        }

        cell.setCellValue(
                String.valueOf(value)
        );
    }

    private void autoSizeColumns(
            Sheet sheet,
            int columnCount
    ) {
        for (int index = 0;
             index < columnCount;
             index++) {

            sheet.autoSizeColumn(index);
        }
    }
}