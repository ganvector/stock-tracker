package com.ganvector.stocktracker.controller;

import com.ganvector.stocktracker.dto.response.PortfolioYieldSummary;
import com.ganvector.stocktracker.dto.response.YieldResponse;
import com.ganvector.stocktracker.service.YieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/yield")
@RequiredArgsConstructor
public class YieldController {

    private final YieldService yieldService;

    @GetMapping
    public PortfolioYieldSummary getPortfolioYield() {
        return yieldService.getPortfolioYield();
    }

    @GetMapping("/{ticker}")
    public YieldResponse getAssetYield(@PathVariable String ticker) {
        return yieldService.getAssetYield(ticker);
    }
}
