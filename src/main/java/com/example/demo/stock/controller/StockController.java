package com.example.demo.stock.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.common.security.JwtTokenProvider;
import com.example.demo.stock.dto.req.OrderBuyRequest;
import com.example.demo.stock.dto.req.OrderSellRequest;
import com.example.demo.stock.dto.res.OrderResponse;
import com.example.demo.stock.dto.res.RoundResultResponse;
import com.example.demo.stock.dto.res.StockRoundDataResponse;
import com.example.demo.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{roomId}/orders/buy")
    public ApiResponse<OrderResponse> buyStock(
            @PathVariable Long roomId,
            @RequestBody OrderBuyRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(stockService.buyStock(userId, roomId, request));
    }

    @PostMapping("/{roomId}/orders/sell")
    public ApiResponse<OrderResponse> sellStock(
            @PathVariable Long roomId,
            @RequestBody OrderSellRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(stockService.sellStock(userId, roomId, request));
    }

    @PatchMapping("/{roomId}/rounds/{roundId}")
    public ApiResponse<RoundResultResponse> endRound(
            @PathVariable Long roomId,
            @PathVariable Long roundId
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(stockService.endRound(userId, roomId, roundId));
    }

    @PatchMapping("{roomId}/end")
    public ApiResponse<String> endGame(
            @PathVariable Long roomId
    ){
        Long userId = jwtTokenProvider.getUserIdFromToken();
        stockService.endGame(userId, roomId);
        return ApiResponse.onSuccess("게임이 종료되었습니다.");
    }
}
