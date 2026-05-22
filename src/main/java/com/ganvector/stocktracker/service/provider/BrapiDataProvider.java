package com.ganvector.stocktracker.service.provider;

import com.ganvector.stocktracker.dto.response.AssetQuoteData;
import com.ganvector.stocktracker.dto.response.BrapiQuoteResponse;
import com.ganvector.stocktracker.dto.response.BrapiQuoteResponse.BrapiQuoteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrapiDataProvider implements MarketDataProvider {

    private final WebClient brapiWebClient;

    @Value("${brapi.token:}")
    private String token;

    @Override
    public Optional<AssetQuoteData> fetchQuote(String ticker) {
        try {
            BrapiQuoteResponse response = brapiWebClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/quote/{ticker}");
                        if (token != null && !token.isBlank()) {
                            uriBuilder.queryParam("token", token);
                        }
                        return uriBuilder.build(ticker);
                    })
                    .retrieve()
                    .bodyToMono(BrapiQuoteResponse.class)
                    .block();

            if (response == null || response.results() == null || response.results().isEmpty()) {
                return Optional.empty();
            }

            BrapiQuoteResult result = response.results().getFirst();
            return Optional.of(AssetQuoteData.builder()
                    .ticker(result.symbol())
                    .shortName(result.shortName())
                    .longName(result.longName())
                    .currency(result.currency())
                    .regularMarketPrice(result.regularMarketPrice())
                    .logoUrl(result.logourl())
                    .source(getSourceName())
                    .build());
        } catch (Exception e) {
            log.warn("BRAPI failed for {}: {}", ticker, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String getSourceName() {
        return "BRAPI";
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
