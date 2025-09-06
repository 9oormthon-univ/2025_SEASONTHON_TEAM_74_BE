package com.example.demo.stock.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record StockRoundDataResponse(
        Integer roundNumber,
        Long teamId,
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
            long price
    ) {
    }
    
    @Builder
    public record TeamAssetDto(
            long currentMoney,
            long totalAsset,
            List<HeldStockDto> heldStocks
    ) {
    }
    
    @Builder
    public record HeldStockDto(
            Long instrumentId,
            String affiliate,
            String uiLabel,
            Integer qty,
            long currentPrice,
            long totalValue
    ) {
    }
}
