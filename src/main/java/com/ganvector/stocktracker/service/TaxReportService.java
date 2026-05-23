package com.ganvector.stocktracker.service;

import com.ganvector.stocktracker.domain.document.Asset;
import com.ganvector.stocktracker.domain.document.Transaction;
import com.ganvector.stocktracker.domain.enums.TransactionType;
import com.ganvector.stocktracker.dto.response.TaxReportEntry;
import com.ganvector.stocktracker.dto.response.TaxReportResponse;
import com.ganvector.stocktracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxReportService {

    private final AssetService assetService;
    private final TransactionRepository transactionRepository;

    public TaxReportResponse generateReport(int year) {
        List<Asset> assets = assetService.findAll();
        List<TaxReportEntry> entries = new ArrayList<>();

        LocalDateTime yearStart = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime yearEnd = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        for (Asset asset : assets) {
            TaxReportEntry entry = buildEntryForAsset(asset, year, yearStart, yearEnd);
            entries.add(entry);
        }

        return TaxReportResponse.builder()
                .referenceYear(year)
                .entries(entries)
                .build();
    }

    private TaxReportEntry buildEntryForAsset(Asset asset, int year,
                                               LocalDateTime yearStart, LocalDateTime yearEnd) {
        List<Transaction> priorTransactions = transactionRepository
                .findByTickerAndTransactionDateBeforeOrderByTransactionDateAsc(
                        asset.getTicker(), yearStart);

        int qtyBefore = 0;
        BigDecimal avgBefore = BigDecimal.ZERO;
        for (Transaction tx : priorTransactions) {
            if (tx.getType() == TransactionType.BUY) {
                BigDecimal currentTotal = avgBefore.multiply(BigDecimal.valueOf(qtyBefore));
                BigDecimal newTotal = tx.getPricePerUnit().multiply(BigDecimal.valueOf(tx.getQuantity()));
                qtyBefore += tx.getQuantity();
                if (qtyBefore > 0) {
                    avgBefore = currentTotal.add(newTotal)
                            .divide(BigDecimal.valueOf(qtyBefore), 4, RoundingMode.HALF_UP);
                }
            } else {
                qtyBefore -= tx.getQuantity();
                if (qtyBefore == 0) {
                    avgBefore = BigDecimal.ZERO;
                }
            }
        }
        BigDecimal valuePreviousYear = avgBefore.multiply(BigDecimal.valueOf(qtyBefore))
                .setScale(2, RoundingMode.HALF_UP);

        List<Transaction> yearTransactions = transactionRepository
                .findByTickerAndTransactionDateBetweenOrderByTransactionDateAsc(
                        asset.getTicker(), yearStart, yearEnd);

        BigDecimal totalPurchased = BigDecimal.ZERO;
        BigDecimal totalSold = BigDecimal.ZERO;
        int totalBoughtQty = 0;
        int totalSoldQty = 0;
        int qtyAfter = qtyBefore;
        BigDecimal avgAfter = avgBefore;

        for (Transaction tx : yearTransactions) {
            if (tx.getType() == TransactionType.BUY) {
                totalPurchased = totalPurchased.add(tx.getTotalValue());
                totalBoughtQty += tx.getQuantity();

                BigDecimal currentTotal = avgAfter.multiply(BigDecimal.valueOf(qtyAfter));
                BigDecimal newTotal = tx.getPricePerUnit().multiply(BigDecimal.valueOf(tx.getQuantity()));
                qtyAfter += tx.getQuantity();
                if (qtyAfter > 0) {
                    avgAfter = currentTotal.add(newTotal)
                            .divide(BigDecimal.valueOf(qtyAfter), 4, RoundingMode.HALF_UP);
                }
            } else {
                totalSold = totalSold.add(tx.getTotalValue());
                totalSoldQty += tx.getQuantity();
                qtyAfter -= tx.getQuantity();
                if (qtyAfter == 0) {
                    avgAfter = BigDecimal.ZERO;
                }
            }
        }

        BigDecimal valueCurrentYear = avgAfter.multiply(BigDecimal.valueOf(qtyAfter))
                .setScale(2, RoundingMode.HALF_UP);

        return TaxReportEntry.builder()
                .ticker(asset.getTicker())
                .companyName(asset.getCompanyName() != null ? asset.getCompanyName() : asset.getName())
                .cnpj(asset.getCnpj())
                .type(asset.getType())
                .quantityPreviousYear(qtyBefore)
                .valuePreviousYear(valuePreviousYear)
                .quantityCurrentYear(qtyAfter)
                .valueCurrentYear(valueCurrentYear)
                .averagePrice(avgAfter)
                .totalPurchased(totalPurchased)
                .totalSold(totalSold)
                .totalBoughtQuantity(totalBoughtQty)
                .totalSoldQuantity(totalSoldQty)
                .build();
    }

    public String generateCsv(TaxReportResponse report) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticker,Empresa,CNPJ,Tipo,Qtd Ano Anterior,Valor Ano Anterior,")
                .append("Qtd Ano Competencia,Valor Ano Competencia,Preco Medio,")
                .append("Total Comprado,Total Vendido,Qtd Comprada,Qtd Vendida\n");

        for (TaxReportEntry entry : report.getEntries()) {
            sb.append(entry.getTicker()).append(",")
                    .append(csvEscape(entry.getCompanyName())).append(",")
                    .append(csvEscape(entry.getCnpj())).append(",")
                    .append(entry.getType()).append(",")
                    .append(entry.getQuantityPreviousYear()).append(",")
                    .append(entry.getValuePreviousYear()).append(",")
                    .append(entry.getQuantityCurrentYear()).append(",")
                    .append(entry.getValueCurrentYear()).append(",")
                    .append(entry.getAveragePrice()).append(",")
                    .append(entry.getTotalPurchased()).append(",")
                    .append(entry.getTotalSold()).append(",")
                    .append(entry.getTotalBoughtQuantity()).append(",")
                    .append(entry.getTotalSoldQuantity()).append("\n");
        }

        return sb.toString();
    }

    public byte[] generateExcel(TaxReportResponse report) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatorio IR " + report.getReferenceYear());

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {
                    "Ticker", "Empresa", "CNPJ", "Tipo",
                    "Qtd Ano Anterior", "Valor Ano Anterior",
                    "Qtd Ano Competência", "Valor Ano Competência",
                    "Preço Médio", "Total Comprado", "Total Vendido",
                    "Qtd Comprada", "Qtd Vendida"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (TaxReportEntry entry : report.getEntries()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(entry.getTicker());
                row.createCell(1).setCellValue(entry.getCompanyName() != null ? entry.getCompanyName() : "");
                row.createCell(2).setCellValue(entry.getCnpj() != null ? entry.getCnpj() : "");
                row.createCell(3).setCellValue(entry.getType().name());
                row.createCell(4).setCellValue(entry.getQuantityPreviousYear());
                row.createCell(5).setCellValue(entry.getValuePreviousYear().doubleValue());
                row.createCell(6).setCellValue(entry.getQuantityCurrentYear());
                row.createCell(7).setCellValue(entry.getValueCurrentYear().doubleValue());
                row.createCell(8).setCellValue(entry.getAveragePrice().doubleValue());
                row.createCell(9).setCellValue(entry.getTotalPurchased().doubleValue());
                row.createCell(10).setCellValue(entry.getTotalSold().doubleValue());
                row.createCell(11).setCellValue(entry.getTotalBoughtQuantity());
                row.createCell(12).setCellValue(entry.getTotalSoldQuantity());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
