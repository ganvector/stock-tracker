package com.ganvector.stocktracker.controller;

import com.ganvector.stocktracker.dto.request.TransactionRequest;
import com.ganvector.stocktracker.dto.response.TransactionResponse;
import com.ganvector.stocktracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse execute(@Valid @RequestBody TransactionRequest request) {
        return TransactionResponse.from(transactionService.executeTransaction(request));
    }

    @GetMapping("/{ticker}")
    public List<TransactionResponse> getHistory(@PathVariable String ticker) {
        return transactionService.getTransactionHistory(ticker).stream()
                .map(TransactionResponse::from)
                .toList();
    }
}
