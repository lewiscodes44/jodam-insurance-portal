package ke.co.jodam.insurance.controller;

import ke.co.jodam.insurance.dto.reporting.AnalyticsSummaryResponse;
import ke.co.jodam.insurance.dto.reporting.DashboardSummaryResponse;
import ke.co.jodam.insurance.dto.reporting.InquiryReportResponse;
import ke.co.jodam.insurance.dto.reporting.PaymentReportResponse;
import ke.co.jodam.insurance.dto.reporting.PerformanceReportResponse;
import ke.co.jodam.insurance.dto.reporting.PolicyReportResponse;
import ke.co.jodam.insurance.service.ReportingExcelExportService;
import ke.co.jodam.insurance.service.ReportingExportService;
import ke.co.jodam.insurance.service.ReportingPdfExportService;
import ke.co.jodam.insurance.service.ReportingService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportingController {

    private final ReportingService reportingService;
    private final ReportingExportService reportingExportService;
    private final ReportingExcelExportService reportingExcelExportService;
    private final ReportingPdfExportService reportingPdfExportService;

    public ReportingController(
            ReportingService reportingService,
            ReportingExportService reportingExportService,
            ReportingExcelExportService reportingExcelExportService,
            ReportingPdfExportService reportingPdfExportService
    ) {
        this.reportingService = reportingService;
        this.reportingExportService =
                reportingExportService;
        this.reportingExcelExportService =
                reportingExcelExportService;
        this.reportingPdfExportService =
                reportingPdfExportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryResponse>
    getDashboardSummary() {

        DashboardSummaryResponse response =
                reportingService.getDashboardSummary();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/policies")
    public ResponseEntity<List<PolicyReportResponse>>
    getPolicyReport() {

        List<PolicyReportResponse> response =
                reportingService.getPolicyReport();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentReportResponse>>
    getPaymentReport() {

        List<PaymentReportResponse> response =
                reportingService.getPaymentReport();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/inquiries")
    public ResponseEntity<List<InquiryReportResponse>>
    getInquiryReport() {

        List<InquiryReportResponse> response =
                reportingService.getInquiryReport();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsSummaryResponse>
    getAnalyticsSummary() {

        AnalyticsSummaryResponse response =
                reportingService.getAnalyticsSummary();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/performance/monthly")
    public ResponseEntity<List<PerformanceReportResponse>>
    getMonthlyPerformanceReport() {

        List<PerformanceReportResponse> response =
                reportingService
                        .getMonthlyPerformanceReport();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/performance/annual")
    public ResponseEntity<List<PerformanceReportResponse>>
    getAnnualPerformanceReport() {

        List<PerformanceReportResponse> response =
                reportingService
                        .getAnnualPerformanceReport();

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/export/policies.csv",
            produces = "text/csv"
    )
    public ResponseEntity<byte[]> exportPoliciesCsv() {

        byte[] csv =
                reportingExportService
                        .exportPoliciesCsv();

        return csvResponse(
                csv,
                "policies-report.csv"
        );
    }

    @GetMapping(
            value = "/export/payments.csv",
            produces = "text/csv"
    )
    public ResponseEntity<byte[]> exportPaymentsCsv() {

        byte[] csv =
                reportingExportService
                        .exportPaymentsCsv();

        return csvResponse(
                csv,
                "payments-report.csv"
        );
    }

    @GetMapping(
            value = "/export/inquiries.csv",
            produces = "text/csv"
    )
    public ResponseEntity<byte[]> exportInquiriesCsv() {

        byte[] csv =
                reportingExportService
                        .exportInquiriesCsv();

        return csvResponse(
                csv,
                "inquiries-report.csv"
        );
    }

    @GetMapping(
            value = "/export/analytics.csv",
            produces = "text/csv"
    )
    public ResponseEntity<byte[]> exportAnalyticsCsv() {

        byte[] csv =
                reportingExportService
                        .exportAnalyticsCsv();

        return csvResponse(
                csv,
                "analytics-report.csv"
        );
    }

    @GetMapping(
            value = "/export/performance-monthly.csv",
            produces = "text/csv"
    )
    public ResponseEntity<byte[]>
    exportMonthlyPerformanceCsv() {

        byte[] csv =
                reportingExportService
                        .exportMonthlyPerformanceCsv();

        return csvResponse(
                csv,
                "performance-monthly-report.csv"
        );
    }

    @GetMapping(
            value = "/export/performance-annual.csv",
            produces = "text/csv"
    )
    public ResponseEntity<byte[]>
    exportAnnualPerformanceCsv() {

        byte[] csv =
                reportingExportService
                        .exportAnnualPerformanceCsv();

        return csvResponse(
                csv,
                "performance-annual-report.csv"
        );
    }

    @GetMapping(
            value = "/export/excel",
            produces =
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public ResponseEntity<byte[]> exportExcel() {

        byte[] workbook =
                reportingExcelExportService
                        .exportWorkbook();

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );

        headers.setContentLength(
                workbook.length
        );

        headers.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"jodam-insurance-report.xlsx\""
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(workbook);
    }

    @GetMapping(
            value = "/export/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> exportPdf() {

        byte[] pdf =
                reportingPdfExportService
                        .exportReport();

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_PDF
        );

        headers.setContentLength(
                pdf.length
        );

        headers.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"jodam-insurance-report.pdf\""
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }

    private ResponseEntity<byte[]> csvResponse(
            byte[] csv,
            String filename
    ) {
        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        "text/csv;charset=UTF-8"
                )
        );

        headers.setContentLength(
                csv.length
        );

        headers.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\""
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(csv);
    }
}