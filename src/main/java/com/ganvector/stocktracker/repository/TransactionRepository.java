package com.ganvector.stocktracker.repository;

import com.ganvector.stocktracker.domain.document.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByAssetIdOrderByTransactionDateDesc(String assetId);

    List<Transaction> findByTickerOrderByTransactionDateDesc(String ticker);

    List<Transaction> findByTickerAndTransactionDateBetweenOrderByTransactionDateAsc(
            String ticker, LocalDateTime start, LocalDateTime end);

    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateAsc(
            LocalDateTime start, LocalDateTime end);

    List<Transaction> findByTickerAndTransactionDateBeforeOrderByTransactionDateAsc(
            String ticker, LocalDateTime before);
}
