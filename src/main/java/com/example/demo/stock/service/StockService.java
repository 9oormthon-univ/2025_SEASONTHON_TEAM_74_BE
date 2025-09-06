package com.example.demo.stock.service;

import com.example.demo.stock.dto.res.StockRoundDataResponse;

public interface StockService {

    StockRoundDataResponse retrieveRoundDate(Long userId, Long roomId, Long roundId);
}
