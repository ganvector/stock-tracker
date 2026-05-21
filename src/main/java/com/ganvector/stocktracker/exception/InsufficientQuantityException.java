package com.ganvector.stocktracker.exception;

public class InsufficientQuantityException extends RuntimeException {

    public InsufficientQuantityException(String ticker, int available, int requested) {
        super("Insufficient quantity for '%s': available=%d, requested=%d".formatted(ticker, available, requested));
    }
}
