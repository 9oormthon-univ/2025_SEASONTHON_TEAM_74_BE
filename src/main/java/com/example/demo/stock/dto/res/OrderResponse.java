package com.example.demo.stock.dto.res;

import com.example.demo.room.entity.Team;
import com.example.demo.stock.entity.Order;
import com.example.demo.stock.entity.enums.Side;
import lombok.Builder;

@Builder
public record OrderResponse(
        Long orderId,
        Long teamId,
        String side,
        Integer price,
        Integer qty,
        Long instrumentId
) {
    public static OrderResponse of(Order order, Team team, Side side, int price, int qty, Long instrumentId) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .teamId(team.getId())
                .side(side.name())
                .price(price)
                .qty(qty)
                .instrumentId(instrumentId)
                .build();
    }
}
