package com.ganvector.stocktracker.controller;

import com.ganvector.stocktracker.domain.enums.AssetType;
import com.ganvector.stocktracker.dto.request.AssetRegistrationRequest;
import com.ganvector.stocktracker.dto.response.AssetResponse;
import com.ganvector.stocktracker.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetResponse register(@Valid @RequestBody AssetRegistrationRequest request) {
        return AssetResponse.from(assetService.registerAsset(request.ticker()));
    }

    @GetMapping
    public List<AssetResponse> listAll(@RequestParam(required = false) AssetType type) {
        if (type != null) {
            return assetService.findByType(type).stream().map(AssetResponse::from).toList();
        }
        return assetService.findAll().stream().map(AssetResponse::from).toList();
    }

    @GetMapping("/{ticker}")
    public AssetResponse findByTicker(@PathVariable String ticker) {
        return AssetResponse.from(assetService.findByTicker(ticker));
    }
}
