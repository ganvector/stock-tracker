package com.ganvector.stocktracker.service;

import com.ganvector.stocktracker.dto.response.BrapiQuoteResponse;
import com.ganvector.stocktracker.dto.response.BrapiQuoteResponse.BrapiQuoteResult;
import com.ganvector.stocktracker.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrapiService {

    private final WebClient brapiWebClient;

    @Value("${brapi.token:}")
    private String token;

    public BrapiQuoteResult fetchQuote(String ticker) {
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
                throw new ExternalApiException("No data found for ticker: " + ticker);
            }

            return response.results().getFirst();
        } catch (WebClientResponseException e) {
            log.error("BRAPI API error for ticker {}: {}", ticker, e.getMessage());
            throw new ExternalApiException("Failed to fetch data for ticker: " + ticker, e);
        }
    }
}
