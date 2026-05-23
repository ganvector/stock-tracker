package com.ganvector.stocktracker.dto.response;

import com.ganvector.stocktracker.domain.enums.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxReportEntry {

    private String ticker;
    private String companyName;
    private String cnpj;
    private AssetType type;
    private int quantityPreviousYear;
    private BigDecimal valuePreviousYear;
    private int quantityCurrentYear;
    private BigDecimal valueCurrentYear;
    private BigDecimal averagePrice;
    private BigDecimal totalPurchased;
    private BigDecimal totalSold;
    private int totalBoughtQuantity;
    private int totalSoldQuantity;
}
