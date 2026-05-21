package com.ganvector.stocktracker.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrapiQuoteResponse(List<BrapiQuoteResult> results) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BrapiQuoteResult(
            String symbol,
            String shortName,
            String longName,
            String currency,
            BigDecimal regularMarketPrice,
            String logourl
    ) {}
}
