package com.ganvector.stocktracker.controller;

import com.ganvector.stocktracker.dto.response.TaxReportResponse;
import com.ganvector.stocktracker.service.TaxReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Year;

@RestController
@RequestMapping("/api/reports/tax")
@RequiredArgsConstructor
public class TaxReportController {

    private final TaxReportService taxReportService;

    @GetMapping
    public TaxReportResponse getReport(@RequestParam(required = false) Integer year) {
        int reportYear = year != null ? year : Year.now().getValue();
        return taxReportService.generateReport(reportYear);
    }

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<String> getCsv(@RequestParam(required = false) Integer year) {
        int reportYear = year != null ? year : Year.now().getValue();
        TaxReportResponse report = taxReportService.generateReport(reportYear);
        String csv = taxReportService.generateCsv(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio_ir_%d.csv".formatted(reportYear))
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> getExcel(@RequestParam(required = false) Integer year) throws IOException {
        int reportYear = year != null ? year : Year.now().getValue();
        TaxReportResponse report = taxReportService.generateReport(reportYear);
        byte[] excel = taxReportService.generateExcel(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio_ir_%d.xlsx".formatted(reportYear))
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
