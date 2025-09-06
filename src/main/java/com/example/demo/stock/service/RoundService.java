package com.example.demo.stock.service;


import com.example.demo.stock.dto.res.RoundRes;

public interface RoundService {



    RoundRes.RoundLockRes lockRound(Long roomId, Long roundId);
}
