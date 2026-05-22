package com.ganvector.stocktracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetQuoteData {

    private String ticker;
    private String shortName;
    private String longName;
    private String companyName;
    private String cnpj;
    private String currency;
    private BigDecimal regularMarketPrice;
    private String logoUrl;
    private BigDecimal dividendYield;
    private BigDecimal lastDividendValue;
    private String source;
}
