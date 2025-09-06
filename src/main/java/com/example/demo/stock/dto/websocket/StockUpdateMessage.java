package com.example.demo.stock.dto.websocket;

import com.example.demo.stock.entity.enums.Side;
import lombok.Builder;

import java.util.List;

@Builder
public record StockUpdateMessage(
        UpdateType type,
        Long roomId,
        Long teamId,
        String teamName,
        TeamAssetUpdate teamAsset,
        OrderUpdate orderUpdate
) {
    public enum UpdateType {
        TEAM_ASSET_UPDATE,    // 팀 자산 변경
        ORDER_EXECUTED        // 주문 체결
    }

    @Builder
    public record TeamAssetUpdate(
            Integer currentMoney,
            Integer totalAsset,
            List<HeldStockUpdate> heldStocks
    ) {
    }

    @Builder
    public record HeldStockUpdate(
            Long instrumentId,
            String affiliate,
            String uiLabel,
            Integer qty,
            Integer currentPrice,
            Integer totalValue
    ) {
    }

    @Builder
    public record OrderUpdate(
            Long orderId,
            Side side,
            Long instrumentId,
            String instrumentName,
            Integer price,
            Integer qty,
            Long timestamp
    ) {
    }
}
