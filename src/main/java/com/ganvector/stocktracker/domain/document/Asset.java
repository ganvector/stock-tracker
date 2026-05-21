package com.ganvector.stocktracker.domain.document;

import com.ganvector.stocktracker.domain.enums.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "assets")
public class Asset {

    @Id
    private String id;

    @Indexed(unique = true)
    private String ticker;

    private String name;

    private AssetType type;

    private String currency;

    private String logoUrl;

    private BigDecimal currentPrice;

    private int totalQuantity;

    private BigDecimal averagePrice;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
