package com.ganvector.stocktracker.service;

import com.ganvector.stocktracker.domain.document.Asset;
import com.ganvector.stocktracker.dto.response.PortfolioYieldSummary;
import com.ganvector.stocktracker.dto.response.YieldResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YieldService {

    private final AssetService assetService;

    public PortfolioYieldSummary getPortfolioYield() {
        List<Asset> assets = assetService.findAll();

        BigDecimal totalPortfolioValue = BigDecimal.ZERO;
        for (Asset asset : assets) {
            if (asset.getTotalQuantity() > 0 && asset.getCurrentPrice() != null) {
                totalPortfolioValue = totalPortfolioValue.add(
                        asset.getCurrentPrice().multiply(BigDecimal.valueOf(asset.getTotalQuantity())));
            }
        }

        List<YieldResponse> yieldEntries = new ArrayList<>();
        BigDecimal totalMonthly = BigDecimal.ZERO;
        BigDecimal totalAnnual = BigDecimal.ZERO;

        for (Asset asset : assets) {
            if (asset.getTotalQuantity() <= 0) continue;

            YieldResponse entry = buildYieldEntry(asset, totalPortfolioValue);
            yieldEntries.add(entry);

            if (entry.getEstimatedMonthlyIncome() != null) {
                totalMonthly = totalMonthly.add(entry.getEstimatedMonthlyIncome());
            }
            if (entry.getEstimatedAnnualIncome() != null) {
                totalAnnual = totalAnnual.add(entry.getEstimatedAnnualIncome());
            }
        }

        BigDecimal avgDy = BigDecimal.ZERO;
        if (totalPortfolioValue.compareTo(BigDecimal.ZERO) > 0 && totalAnnual.compareTo(BigDecimal.ZERO) > 0) {
            avgDy = totalAnnual.divide(totalPortfolioValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return PortfolioYieldSummary.builder()
                .totalPortfolioValue(totalPortfolioValue.setScale(2, RoundingMode.HALF_UP))
                .totalEstimatedMonthlyIncome(totalMonthly.setScale(2, RoundingMode.HALF_UP))
                .totalEstimatedAnnualIncome(totalAnnual.setScale(2, RoundingMode.HALF_UP))
                .averageDividendYield(avgDy.setScale(2, RoundingMode.HALF_UP))
                .assets(yieldEntries)
                .build();
    }

    public YieldResponse getAssetYield(String ticker) {
        Asset asset = assetService.findByTicker(ticker);
        List<Asset> all = assetService.findAll();

        BigDecimal totalPortfolioValue = BigDecimal.ZERO;
        for (Asset a : all) {
            if (a.getTotalQuantity() > 0 && a.getCurrentPrice() != null) {
                totalPortfolioValue = totalPortfolioValue.add(
                        a.getCurrentPrice().multiply(BigDecimal.valueOf(a.getTotalQuantity())));
            }
        }

        return buildYieldEntry(asset, totalPortfolioValue);
    }

    private YieldResponse buildYieldEntry(Asset asset, BigDecimal totalPortfolioValue) {
        BigDecimal positionValue = BigDecimal.ZERO;
        if (asset.getCurrentPrice() != null) {
            positionValue = asset.getCurrentPrice()
                    .multiply(BigDecimal.valueOf(asset.getTotalQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal portfolioPercentage = BigDecimal.ZERO;
        if (totalPortfolioValue.compareTo(BigDecimal.ZERO) > 0) {
            portfolioPercentage = positionValue
                    .divide(totalPortfolioValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal estimatedAnnual = BigDecimal.ZERO;
        BigDecimal estimatedMonthly = BigDecimal.ZERO;

        if (asset.getLastDividendValue() != null && asset.getLastDividendValue().compareTo(BigDecimal.ZERO) > 0) {
            estimatedAnnual = asset.getLastDividendValue()
                    .multiply(BigDecimal.valueOf(asset.getTotalQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            estimatedMonthly = estimatedAnnual
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        } else if (asset.getDividendYield() != null && asset.getCurrentPrice() != null
                && asset.getDividendYield().compareTo(BigDecimal.ZERO) > 0) {
            estimatedAnnual = positionValue
                    .multiply(asset.getDividendYield())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            estimatedMonthly = estimatedAnnual
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        }

        BigDecimal yieldOnCost = BigDecimal.ZERO;
        if (asset.getAveragePrice() != null && asset.getAveragePrice().compareTo(BigDecimal.ZERO) > 0
                && asset.getLastDividendValue() != null) {
            yieldOnCost = asset.getLastDividendValue()
                    .divide(asset.getAveragePrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return YieldResponse.builder()
                .ticker(asset.getTicker())
                .companyName(asset.getCompanyName() != null ? asset.getCompanyName() : asset.getName())
                .type(asset.getType())
                .currentPrice(asset.getCurrentPrice())
                .totalQuantity(asset.getTotalQuantity())
                .positionValue(positionValue)
                .portfolioPercentage(portfolioPercentage)
                .dividendYield(asset.getDividendYield())
                .lastDividendValue(asset.getLastDividendValue())
                .estimatedMonthlyIncome(estimatedMonthly)
                .estimatedAnnualIncome(estimatedAnnual)
                .averagePrice(asset.getAveragePrice())
                .yieldOnCost(yieldOnCost)
                .build();
    }
}
