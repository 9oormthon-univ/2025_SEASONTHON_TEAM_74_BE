package com.example.demo.stock.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.common.security.JwtTokenProvider;
import com.example.demo.stock.dto.res.RoundRes;
import com.example.demo.stock.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoundController {

    private final JwtTokenProvider jwtTokenProvider;

    private final RoundService roundService;

    @PostMapping("/{roomId}/rounds/{roundId}/lock")
    public ApiResponse<RoundRes.RoundLockRes> lockRound(@PathVariable Long roomId, @PathVariable Long roundId) {
        return ApiResponse.onSuccess(roundService.lockRound(roomId, roundId));

    }




}
