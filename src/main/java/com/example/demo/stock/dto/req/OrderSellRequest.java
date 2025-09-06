package com.example.demo.stock.dto.req;

public record OrderSellRequest(
        Long instrumentId,
        Integer qty
) {
}
