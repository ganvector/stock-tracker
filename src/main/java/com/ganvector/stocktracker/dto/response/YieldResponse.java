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
public class YieldResponse {

    private String ticker;
    private String companyName;
    private AssetType type;
    private BigDecimal currentPrice;
    private int totalQuantity;
    private BigDecimal positionValue;
    private BigDecimal portfolioPercentage;
    private BigDecimal dividendYield;
    private BigDecimal lastDividendValue;
    private BigDecimal estimatedMonthlyIncome;
    private BigDecimal estimatedAnnualIncome;
    private BigDecimal averagePrice;
    private BigDecimal yieldOnCost;
}
