package com.ganvector.stocktracker.service.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ganvector.stocktracker.dto.response.AssetQuoteData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class YahooFinanceDataProvider implements MarketDataProvider {

    private static final String BASE_URL = "https://query1.finance.yahoo.com";
    private final WebClient webClient;

    public YahooFinanceDataProvider() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();
    }

    @Override
    public Optional<AssetQuoteData> fetchQuote(String ticker) {
        try {
            String yahooTicker = ticker + ".SA";

            YahooResponse response = webClient.get()
                    .uri("/v7/finance/quote?symbols={ticker}", yahooTicker)
                    .retrieve()
                    .bodyToMono(YahooResponse.class)
                    .block();

            if (response == null || response.quoteResponse() == null
                    || response.quoteResponse().result() == null
                    || response.quoteResponse().result().isEmpty()) {
                return Optional.empty();
            }

            YahooQuote quote = response.quoteResponse().result().getFirst();

            return Optional.of(AssetQuoteData.builder()
                    .ticker(ticker)
                    .shortName(quote.shortName())
                    .longName(quote.longName())
                    .currency(quote.currency())
                    .regularMarketPrice(quote.regularMarketPrice())
                    .dividendYield(quote.trailingAnnualDividendYield() != null
                            ? quote.trailingAnnualDividendYield().multiply(BigDecimal.valueOf(100))
                            : null)
                    .lastDividendValue(quote.trailingAnnualDividendRate())
                    .source(getSourceName())
                    .build());
        } catch (Exception e) {
            log.warn("Yahoo Finance failed for {}: {}", ticker, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String getSourceName() {
        return "YahooFinance";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record YahooResponse(YahooQuoteResponse quoteResponse) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record YahooQuoteResponse(List<YahooQuote> result) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record YahooQuote(
            String symbol,
            String shortName,
            String longName,
            String currency,
            BigDecimal regularMarketPrice,
            BigDecimal trailingAnnualDividendYield,
            BigDecimal trailingAnnualDividendRate
    ) {}
}
