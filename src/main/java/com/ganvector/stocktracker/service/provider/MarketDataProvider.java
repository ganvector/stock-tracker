package com.ganvector.stocktracker.service.provider;

import com.ganvector.stocktracker.dto.response.AssetQuoteData;

import java.util.Optional;

public interface MarketDataProvider {

    Optional<AssetQuoteData> fetchQuote(String ticker);

    String getSourceName();

    int getPriority();
}
