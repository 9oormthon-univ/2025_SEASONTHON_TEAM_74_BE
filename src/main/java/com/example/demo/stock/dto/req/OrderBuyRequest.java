package com.example.demo.stock.dto.req;

public record OrderBuyRequest(
        Long instrumentId,
        Integer qty
) {
}
