package com.example.demo.stock.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class RoundRes {

    //주문 잠금 페이지
    public record RoundLockRes(
            Long teamId,
            String teamName,
            String maximumStockName,
            Integer maximumStockMoney,
            String status
    ) {}

}
