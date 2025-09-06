package com.example.demo.stock.service;

import com.example.demo.room.entity.Team;
import com.example.demo.room.repository.TeamMemberRepository;
import com.example.demo.stock.dto.res.StockRoundDataResponse;
import com.example.demo.stock.entity.Round;
import com.example.demo.stock.entity.StockHeld;
import com.example.demo.stock.entity.Year;
import com.example.demo.stock.repository.RoundRepository;
import com.example.demo.stock.repository.StockHeldRepository;
import com.example.demo.stock.repository.YearInstrumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final RoundRepository roundRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final YearInstrumentRepository yearInstrumentRepository;
    private final StockHeldRepository stockHeldRepository;

    @Override
    @Transactional(readOnly = true)
    public StockRoundDataResponse retrieveRoundDate(Long userId, Long roomId, Long roundId) {
        Round round = getRoundByRoomAndRoundId(roomId, roundId);
        Team userTeam = getUserTeam(userId, roomId);
        Year year = round.getYear();

        List<StockRoundDataResponse.StockInfoDto> stocks = buildStockInfoList(year.getId());
        StockRoundDataResponse.TeamAssetDto teamAsset = buildTeamAssetInfo(userTeam);

        return StockRoundDataResponse.builder()
                .roundNumber(round.getRoundNumber())
                .year(year.getYear())
                .hint1(year.getHint1())
                .hint2(year.getHint2())
                .hint3(year.getHint3())
                .stocks(stocks)
                .teamAsset(teamAsset)
                .build();
    }

    private Round getRoundByRoomAndRoundId(Long roomId, Long roundId) {
        return roundRepository.findByRoomIdAndRoundId(roomId, roundId)
                .orElseThrow(() -> new RuntimeException("라운드를 찾을 수 없습니다."));
    }

    private Team getUserTeam(Long userId, Long roomId) {
        return teamMemberRepository.findTeamByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("사용자의 팀을 찾을 수 없습니다."));
    }

    private List<StockRoundDataResponse.StockInfoDto> buildStockInfoList(Long yearId) {
        return yearInstrumentRepository.findByYearId(yearId).stream()
                .map(yi -> StockRoundDataResponse.StockInfoDto.builder()
                        .instrumentId(yi.getInstrument().getId())
                        .uiLabel(yi.getInstrument().getUiLabel())
                        .price(yi.getPrice())
                        .build())
                .toList();
    }

    private StockRoundDataResponse.TeamAssetDto buildTeamAssetInfo(Team team) {
        List<StockHeld> heldStocks = stockHeldRepository.findByTeamId(team.getId());

        int totalStockValue = heldStocks.stream()
                .mapToInt(sh -> sh.getQty() * sh.getYearInstrument().getPrice())
                .sum();

        List<StockRoundDataResponse.HeldStockDto> heldStockDtos = heldStocks.stream()
                .map(sh -> StockRoundDataResponse.HeldStockDto.builder()
                        .instrumentId(sh.getYearInstrument().getInstrument().getId())
                        .affiliate(sh.getYearInstrument().getInstrument().getAffiliate())
                        .uiLabel(sh.getYearInstrument().getInstrument().getUiLabel())
                        .qty(sh.getQty())
                        .currentPrice(sh.getYearInstrument().getPrice())
                        .totalValue(sh.getQty() * sh.getYearInstrument().getPrice())
                        .build())
                .toList();

        return StockRoundDataResponse.TeamAssetDto.builder()
                .currentMoney(team.getAsset())
                .totalAsset(team.getAsset() + totalStockValue)
                .heldStocks(heldStockDtos)
                .build();
    }
}
