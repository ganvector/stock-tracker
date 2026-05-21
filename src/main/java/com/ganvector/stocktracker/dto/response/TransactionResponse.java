package com.ganvector.stocktracker.dto.response;

import com.ganvector.stocktracker.domain.document.Transaction;
import com.ganvector.stocktracker.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String assetId,
        String ticker,
        TransactionType type,
        int quantity,
        BigDecimal pricePerUnit,
        BigDecimal totalValue,
        LocalDateTime transactionDate
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAssetId(),
                transaction.getTicker(),
                transaction.getType(),
                transaction.getQuantity(),
                transaction.getPricePerUnit(),
                transaction.getTotalValue(),
                transaction.getTransactionDate()
        );
    }
}
