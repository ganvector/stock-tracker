package com.ganvector.stocktracker.service;

import com.ganvector.stocktracker.domain.enums.AssetType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssetServiceTest {

    @Test
    void shouldClassifyFiiCorrectly() {
        assertEquals(AssetType.FII, AssetService.classifyAsset("HGLG11"));
        assertEquals(AssetType.FII, AssetService.classifyAsset("XPML11"));
        assertEquals(AssetType.FII, AssetService.classifyAsset("MXRF11"));
    }

    @Test
    void shouldClassifyStockCorrectly() {
        assertEquals(AssetType.STOCK, AssetService.classifyAsset("PETR4"));
        assertEquals(AssetType.STOCK, AssetService.classifyAsset("VALE3"));
        assertEquals(AssetType.STOCK, AssetService.classifyAsset("ITUB4"));
        assertEquals(AssetType.STOCK, AssetService.classifyAsset("BBDC3"));
    }
}
