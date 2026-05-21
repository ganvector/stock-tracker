package com.ganvector.stocktracker.service;

import com.ganvector.stocktracker.domain.document.Asset;
import com.ganvector.stocktracker.domain.document.Transaction;
import com.ganvector.stocktracker.domain.enums.TransactionType;
import com.ganvector.stocktracker.dto.request.TransactionRequest;
import com.ganvector.stocktracker.exception.InsufficientQuantityException;
import com.ganvector.stocktracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AssetService assetService;

    public Transaction executeTransaction(TransactionRequest request) {
        Asset asset = assetService.findByTicker(request.ticker());

        if (request.type() == TransactionType.SELL && asset.getTotalQuantity() < request.quantity()) {
            throw new InsufficientQuantityException(
                    asset.getTicker(), asset.getTotalQuantity(), request.quantity());
        }

        BigDecimal totalValue = request.pricePerUnit().multiply(BigDecimal.valueOf(request.quantity()));

        Transaction transaction = Transaction.builder()
                .assetId(asset.getId())
                .ticker(asset.getTicker())
                .type(request.type())
                .quantity(request.quantity())
                .pricePerUnit(request.pricePerUnit())
                .totalValue(totalValue)
                .transactionDate(request.transactionDate() != null ? request.transactionDate() : LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);

        updateAssetPosition(asset, request.type(), request.quantity(), request.pricePerUnit());

        return transaction;
    }

    public List<Transaction> getTransactionHistory(String ticker) {
        return transactionRepository.findByTickerOrderByTransactionDateDesc(ticker.toUpperCase().trim());
    }

    private void updateAssetPosition(Asset asset, TransactionType type, int quantity, BigDecimal pricePerUnit) {
        if (type == TransactionType.BUY) {
            BigDecimal currentTotal = asset.getAveragePrice()
                    .multiply(BigDecimal.valueOf(asset.getTotalQuantity()));
            BigDecimal newTotal = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
            int newQuantity = asset.getTotalQuantity() + quantity;

            BigDecimal newAveragePrice = currentTotal.add(newTotal)
                    .divide(BigDecimal.valueOf(newQuantity), 4, RoundingMode.HALF_UP);

            asset.setTotalQuantity(newQuantity);
            asset.setAveragePrice(newAveragePrice);
        } else {
            int newQuantity = asset.getTotalQuantity() - quantity;
            asset.setTotalQuantity(newQuantity);
            if (newQuantity == 0) {
                asset.setAveragePrice(BigDecimal.ZERO);
            }
        }

        assetService.save(asset);
    }
}
