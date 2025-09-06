package com.example.demo.stock.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record StockRoundDataResponse(
        Integer roundNumber,
        Long year,
        String hint1,
        String hint2,
        String hint3,
        List<StockInfoDto> stocks,
        TeamAssetDto teamAsset
) {
    
    @Builder
    public record StockInfoDto(
            Long instrumentId,
            String uiLabel,
            Integer price
    ) {
    }
    
    @Builder
    public record TeamAssetDto(
            Integer currentMoney,
            Integer totalAsset,
            List<HeldStockDto> heldStocks
    ) {
    }
    
    @Builder
    public record HeldStockDto(
            Long instrumentId,
            String affiliate,
            String uiLabel,
            Integer qty,
            Integer currentPrice,
            Integer totalValue
    ) {
    }
}
