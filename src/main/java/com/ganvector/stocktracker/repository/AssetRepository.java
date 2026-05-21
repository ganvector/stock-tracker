package com.ganvector.stocktracker.repository;

import com.ganvector.stocktracker.domain.document.Asset;
import com.ganvector.stocktracker.domain.enums.AssetType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends MongoRepository<Asset, String> {

    Optional<Asset> findByTicker(String ticker);

    List<Asset> findByType(AssetType type);

    boolean existsByTicker(String ticker);
}
