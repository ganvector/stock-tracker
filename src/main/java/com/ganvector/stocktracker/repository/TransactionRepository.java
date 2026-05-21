package com.ganvector.stocktracker.repository;

import com.ganvector.stocktracker.domain.document.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByAssetIdOrderByTransactionDateDesc(String assetId);

    List<Transaction> findByTickerOrderByTransactionDateDesc(String ticker);
}
