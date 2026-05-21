package com.ganvector.stocktracker.dto.request;

import com.ganvector.stocktracker.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
        @NotBlank(message = "Ticker is required")
        String ticker,

        @NotNull(message = "Transaction type is required")
        TransactionType type,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,

        @NotNull(message = "Price per unit is required")
        @DecimalMin(value = "0.01", message = "Price per unit must be greater than 0")
        BigDecimal pricePerUnit,

        LocalDateTime transactionDate
) {}
