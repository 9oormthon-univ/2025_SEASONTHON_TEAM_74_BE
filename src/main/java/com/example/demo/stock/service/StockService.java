package com.example.demo.stock.service;

import com.example.demo.stock.dto.req.OrderBuyRequest;
import com.example.demo.stock.dto.req.OrderSellRequest;
import com.example.demo.stock.dto.res.OrderResponse;
import com.example.demo.stock.dto.res.StockRoundDataResponse;

public interface StockService {

    StockRoundDataResponse retrieveRoundDate(Long userId, Long roomId, Long roundId);

    OrderResponse buyStock(Long userId, Long roomId, OrderBuyRequest request);

    OrderResponse sellStock(Long userId, Long roomId, OrderSellRequest request);
}
