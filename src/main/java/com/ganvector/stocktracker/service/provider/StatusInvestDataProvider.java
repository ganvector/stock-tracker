package com.ganvector.stocktracker.service.provider;

import com.ganvector.stocktracker.dto.response.AssetQuoteData;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Component
public class StatusInvestDataProvider implements MarketDataProvider {

    private static final String BASE_URL = "https://statusinvest.com.br";

    @Override
    public Optional<AssetQuoteData> fetchQuote(String ticker) {
        try {
            String assetPath = determineAssetPath(ticker);
            String url = BASE_URL + assetPath + "/" + ticker.toLowerCase();

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept-Language", "pt-BR,pt;q=0.9")
                    .timeout(10000)
                    .get();

            AssetQuoteData.AssetQuoteDataBuilder builder = AssetQuoteData.builder()
                    .ticker(ticker)
                    .source(getSourceName());

            Element companyNameEl = doc.selectFirst("h1.lh-4 small");
            if (companyNameEl != null) {
                builder.companyName(companyNameEl.text().trim());
            }

            Element priceEl = doc.selectFirst("div.top-info div.info strong.value");
            if (priceEl != null) {
                String priceText = priceEl.text().replace(",", ".").trim();
                try {
                    builder.regularMarketPrice(new BigDecimal(priceText));
                } catch (NumberFormatException ignored) {
                    // price parsing failed
                }
            }

            extractCompanyDetails(doc, builder);
            extractDividendInfo(doc, builder);

            return Optional.of(builder.build());
        } catch (Exception e) {
            log.warn("StatusInvest failed for {}: {}", ticker, e.getMessage());
            return Optional.empty();
        }
    }

    private void extractCompanyDetails(Document doc, AssetQuoteData.AssetQuoteDataBuilder builder) {
        Elements rows = doc.select("div.card div.info");
        for (Element row : rows) {
            Element title = row.selectFirst("span.sub-value, h3.title");
            Element value = row.selectFirst("strong.value, a.value");
            if (title == null || value == null) continue;

            String titleText = title.text().trim().toLowerCase();
            String valueText = value.text().trim();

            if (titleText.contains("cnpj")) {
                builder.cnpj(valueText);
            } else if (titleText.contains("razão social") || titleText.contains("nome")) {
                if (builder.build().getCompanyName() == null) {
                    builder.companyName(valueText);
                }
            }
        }
    }

    private void extractDividendInfo(Document doc, AssetQuoteData.AssetQuoteDataBuilder builder) {
        Elements indicators = doc.select("div[title]");
        for (Element indicator : indicators) {
            String titleAttr = indicator.attr("title").toLowerCase();
            Element valueEl = indicator.selectFirst("strong.value");
            if (valueEl == null) continue;

            String valueText = valueEl.text().replace(",", ".").replace("%", "").trim();
            try {
                BigDecimal value = new BigDecimal(valueText);
                if (titleAttr.contains("dividend yield")) {
                    builder.dividendYield(value);
                }
            } catch (NumberFormatException ignored) {
                // value parsing failed
            }
        }
    }

    private String determineAssetPath(String ticker) {
        if (ticker.length() == 6 && ticker.matches("^[A-Z]{4}11$")) {
            return "/fundos-imobiliarios";
        }
        return "/acoes";
    }

    @Override
    public String getSourceName() {
        return "StatusInvest";
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
