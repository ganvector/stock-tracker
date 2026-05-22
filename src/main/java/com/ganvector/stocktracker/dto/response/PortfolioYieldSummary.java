package com.ganvector.stocktracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioYieldSummary {

    private BigDecimal totalPortfolioValue;
    private BigDecimal totalEstimatedMonthlyIncome;
    private BigDecimal totalEstimatedAnnualIncome;
    private BigDecimal averageDividendYield;
    private List<YieldResponse> assets;
}
