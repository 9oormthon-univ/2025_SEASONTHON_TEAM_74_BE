package com.example.demo.stock.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.common.security.JwtTokenProvider;
import com.example.demo.stock.dto.res.StockRoundDataResponse;
import com.example.demo.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.apiPayload.code.status.SuccessStatus.GET_ROUND_DATA;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{roomId}/rounds/{roundId}/data")
    public ApiResponse<StockRoundDataResponse> retrieveStockRoundData(
            @PathVariable Long roomId,
            @PathVariable Long roundId
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(stockService.retrieveRoundDate(userId, roomId, roundId));
    }
}
