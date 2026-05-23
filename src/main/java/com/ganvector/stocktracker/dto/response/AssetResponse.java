package com.ganvector.stocktracker.dto.response;

import com.ganvector.stocktracker.domain.document.Asset;
import com.ganvector.stocktracker.domain.enums.AssetType;

import java.math.BigDecimal;

public record AssetResponse(
        String id,
        String ticker,
        String name,
        String companyName,
        String cnpj,
        AssetType type,
        String currency,
        String logoUrl,
        BigDecimal currentPrice,
        BigDecimal dividendYield,
        BigDecimal lastDividendValue,
        int totalQuantity,
        BigDecimal averagePrice
) {
    public static AssetResponse from(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getTicker(),
                asset.getName(),
                asset.getCompanyName(),
                asset.getCnpj(),
                asset.getType(),
                asset.getCurrency(),
                asset.getLogoUrl(),
                asset.getCurrentPrice(),
                asset.getDividendYield(),
                asset.getLastDividendValue(),
                asset.getTotalQuantity(),
                asset.getAveragePrice()
        );
    }
}
