package com.ganvector.stocktracker.service;

import com.ganvector.stocktracker.dto.response.AssetQuoteData;
import com.ganvector.stocktracker.exception.ExternalApiException;
import com.ganvector.stocktracker.service.provider.MarketDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MarketDataAggregator {

    private final List<MarketDataProvider> providers;

    public MarketDataAggregator(List<MarketDataProvider> providers) {
        this.providers = providers.stream()
                .sorted(Comparator.comparingInt(MarketDataProvider::getPriority))
                .toList();
    }

    public AssetQuoteData fetchQuote(String ticker) {
        AssetQuoteData merged = AssetQuoteData.builder().ticker(ticker).build();
        boolean anySuccess = false;

        for (MarketDataProvider provider : providers) {
            Optional<AssetQuoteData> result = provider.fetchQuote(ticker);
            if (result.isPresent()) {
                anySuccess = true;
                merge(merged, result.get());
                log.info("Fetched data for {} from {}", ticker, provider.getSourceName());
            }
        }

        if (!anySuccess) {
            throw new ExternalApiException("No data found for ticker '%s' from any source".formatted(ticker));
        }

        return merged;
    }

    private void merge(AssetQuoteData target, AssetQuoteData source) {
        if (target.getShortName() == null && source.getShortName() != null) {
            target.setShortName(source.getShortName());
        }
        if (target.getLongName() == null && source.getLongName() != null) {
            target.setLongName(source.getLongName());
        }
        if (target.getCompanyName() == null && source.getCompanyName() != null) {
            target.setCompanyName(source.getCompanyName());
        }
        if (target.getCnpj() == null && source.getCnpj() != null) {
            target.setCnpj(source.getCnpj());
        }
        if (target.getCurrency() == null && source.getCurrency() != null) {
            target.setCurrency(source.getCurrency());
        }
        if (target.getRegularMarketPrice() == null && source.getRegularMarketPrice() != null) {
            target.setRegularMarketPrice(source.getRegularMarketPrice());
        }
        if (target.getLogoUrl() == null && source.getLogoUrl() != null) {
            target.setLogoUrl(source.getLogoUrl());
        }
        if (target.getDividendYield() == null && source.getDividendYield() != null) {
            target.setDividendYield(source.getDividendYield());
        }
        if (target.getLastDividendValue() == null && source.getLastDividendValue() != null) {
            target.setLastDividendValue(source.getLastDividendValue());
        }
        if (target.getSource() == null) {
            target.setSource(source.getSource());
        } else if (source.getSource() != null && !target.getSource().contains(source.getSource())) {
            target.setSource(target.getSource() + "," + source.getSource());
        }
    }
}
