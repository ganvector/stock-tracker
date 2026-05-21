package com.ganvector.stocktracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AssetRegistrationRequest(
        @NotBlank(message = "Ticker is required")
        String ticker
) {}
