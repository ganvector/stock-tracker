package com.ganvector.stocktracker.dto.response;

import com.ganvector.stocktracker.domain.document.Asset;
import com.ganvector.stocktracker.domain.enums.AssetType;

import java.math.BigDecimal;

public record AssetResponse(
        String id,
        String ticker,
        String name,
        AssetType type,
        String currency,
        String logoUrl,
        BigDecimal currentPrice,
        int totalQuantity,
        BigDecimal averagePrice
) {
    public static AssetResponse from(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getTicker(),
                asset.getName(),
                asset.getType(),
                asset.getCurrency(),
                asset.getLogoUrl(),
                asset.getCurrentPrice(),
                asset.getTotalQuantity(),
                asset.getAveragePrice()
        );
    }
}
