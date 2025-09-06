package com.example.demo.stock.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record RoundResultResponse(
        Integer roundNumber,
        Long year,
        List<TeamInvestmentDto> teamInvestments
) {

    @Builder
    public record TeamInvestmentDto(
            String teamName,
            String maxInvestmentStock,
            Integer totalInvestmentAmount
    ) {
    }
}
