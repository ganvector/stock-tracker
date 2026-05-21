package com.ganvector.stocktracker.exception;

public class AssetAlreadyRegisteredException extends RuntimeException {

    public AssetAlreadyRegisteredException(String ticker) {
        super("Asset with ticker '%s' is already registered".formatted(ticker));
    }
}
