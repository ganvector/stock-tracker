package com.ganvector.stocktracker.domain.document;

import com.ganvector.stocktracker.domain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    private String assetId;

    private String ticker;

    private TransactionType type;

    private int quantity;

    private BigDecimal pricePerUnit;

    private BigDecimal totalValue;

    private LocalDateTime transactionDate;

    private LocalDateTime createdAt;
}
