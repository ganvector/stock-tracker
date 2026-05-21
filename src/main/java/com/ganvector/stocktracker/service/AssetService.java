package com.ganvector.stocktracker.service;

import com.ganvector.stocktracker.domain.document.Asset;
import com.ganvector.stocktracker.domain.enums.AssetType;
import com.ganvector.stocktracker.dto.response.BrapiQuoteResponse.BrapiQuoteResult;
import com.ganvector.stocktracker.exception.AssetAlreadyRegisteredException;
import com.ganvector.stocktracker.exception.AssetNotFoundException;
import com.ganvector.stocktracker.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final BrapiService brapiService;

    public Asset registerAsset(String ticker) {
        String normalizedTicker = ticker.toUpperCase().trim();

        if (assetRepository.existsByTicker(normalizedTicker)) {
            throw new AssetAlreadyRegisteredException(normalizedTicker);
        }

        BrapiQuoteResult quote = brapiService.fetchQuote(normalizedTicker);

        AssetType type = classifyAsset(normalizedTicker);

        Asset asset = Asset.builder()
                .ticker(normalizedTicker)
                .name(quote.longName() != null ? quote.longName() : quote.shortName())
                .type(type)
                .currency(quote.currency())
                .logoUrl(quote.logourl())
                .currentPrice(quote.regularMarketPrice())
                .totalQuantity(0)
                .averagePrice(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return assetRepository.save(asset);
    }

    public Asset findByTicker(String ticker) {
        return assetRepository.findByTicker(ticker.toUpperCase().trim())
                .orElseThrow(() -> new AssetNotFoundException(ticker));
    }

    public List<Asset> findAll() {
        return assetRepository.findAll();
    }

    public List<Asset> findByType(AssetType type) {
        return assetRepository.findByType(type);
    }

    public Asset save(Asset asset) {
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    static AssetType classifyAsset(String ticker) {
        if (ticker.length() == 6 && ticker.matches("^[A-Z]{4}11$")) {
            return AssetType.FII;
        }
        return AssetType.STOCK;
    }
}
