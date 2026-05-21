package com.ganvector.stocktracker.exception;

public class AssetNotFoundException extends RuntimeException {

    public AssetNotFoundException(String ticker) {
        super("Asset with ticker '%s' not found".formatted(ticker));
    }
}
