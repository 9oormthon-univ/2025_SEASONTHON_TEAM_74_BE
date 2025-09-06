package com.example.demo.stock.service;

import com.example.demo.room.entity.Room;
import com.example.demo.room.entity.Team;
import com.example.demo.room.entity.enums.RoomStatus;
import com.example.demo.room.repository.RoomRepository;
import com.example.demo.room.repository.TeamMemberRepository;
import com.example.demo.room.repository.TeamRepository;
import com.example.demo.stock.dto.req.OrderBuyRequest;
import com.example.demo.stock.dto.req.OrderSellRequest;
import com.example.demo.stock.dto.res.OrderResponse;
import com.example.demo.stock.dto.res.RoundResultResponse;
import com.example.demo.stock.dto.res.StockRoundDataResponse;
import com.example.demo.stock.entity.*;
import com.example.demo.stock.entity.enums.Side;
import com.example.demo.stock.repository.OrdersRepository;
import com.example.demo.stock.repository.RoundRepository;
import com.example.demo.stock.repository.StockHeldRepository;
import com.example.demo.stock.repository.YearInstrumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final RoundRepository roundRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final YearInstrumentRepository yearInstrumentRepository;
    private final StockHeldRepository stockHeldRepository;
    private final OrdersRepository ordersRepository;
    private final StockWebSocketService stockWebSocketService;
    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public StockRoundDataResponse retrieveRoundDate(Long userId, Long roomId, Long roundId) {
        Round round = getRoundByRoomAndRoundId(roomId, roundId);
        Team userTeam = getUserTeam(userId, roomId);
        Year year = round.getYear();

        List<StockRoundDataResponse.StockInfoDto> stocks = buildStockInfoList(year.getYearId());
        StockRoundDataResponse.TeamAssetDto teamAsset = buildTeamAssetInfo(userTeam);

        return StockRoundDataResponse.builder()
                .roundNumber(round.getRoundNumber())
                .year(year.getYearId())
                .hint1(year.getHint1())
                .hint2(year.getHint2())
                .hint3(year.getHint3())
                .stocks(stocks)
                .teamAsset(teamAsset)
                .build();
    }

    @Override
    public OrderResponse buyStock(Long userId, Long roomId, OrderBuyRequest request) {
        // 1. 기본 데이터 조회
        Round currentRound = getCurrentRoundByRoomId(roomId);
        Team userTeam = getUserTeam(userId, roomId);
        YearInstrument yearInstrument = getYearInstrument(currentRound.getYear().getYearId(), request.instrumentId());
        
        // 2. 동시성 보호를 위한 락 조회
        Team teamForUpdate = getTeamForUpdate(userTeam.getId());
        
        // 3. 거래 정보 계산 및 검증
        int serverPrice = yearInstrument.getPrice();
        int requestQty = request.qty();
        
        validateBuyRequest(requestQty, teamForUpdate, serverPrice);
        
        // 4. 주문 처리
        Order order = createAndSaveBuyOrder(currentRound, teamForUpdate, serverPrice, requestQty);
        
        // 5. 자산 및 포지션 업데이트
        debitTeamAsset(teamForUpdate, serverPrice * requestQty);
        updateStockPosition(teamForUpdate, yearInstrument, order, requestQty);
        
        // 6. 실시간 업데이트 브로드캐스트
        stockWebSocketService.broadcastOrderExecution(order, teamForUpdate, roomId, yearInstrument);

        return OrderResponse.of(
                order, teamForUpdate, Side.BUY,
                serverPrice, requestQty,
                request.instrumentId()
        );
    }

    @Override
    public OrderResponse sellStock(Long userId, Long roomId, OrderSellRequest request) {
        // 1. 기본 데이터 조회
        Round currentRound = getCurrentRoundByRoomId(roomId);
        Team userTeam = getUserTeam(userId, roomId);
        YearInstrument yearInstrument = getYearInstrument(currentRound.getYear().getYearId(), request.instrumentId());
        
        // 2. 동시성 보호를 위한 락 조회
        Team teamForUpdate = getTeamForUpdate(userTeam.getId());
        StockHeld heldStock = getHeldStock(teamForUpdate, yearInstrument);
        
        // 3. 거래 정보 계산 및 검증
        int serverPrice = yearInstrument.getPrice();
        int requestQty = request.qty();
        
        validateSellRequest(requestQty, heldStock);
        
        // 4. 주문 처리
        Order order = createAndSaveSellOrder(currentRound, teamForUpdate, serverPrice, requestQty);
        
        // 5. 자산 및 포지션 업데이트
        creditTeamAsset(teamForUpdate, serverPrice * requestQty);
        updateStockPositionForSell(heldStock, requestQty);
        
        // 6. 실시간 업데이트 브로드캐스트
        stockWebSocketService.broadcastOrderExecution(order, teamForUpdate, roomId, yearInstrument);

        return OrderResponse.of(
                order, teamForUpdate, Side.SELL,
                serverPrice, requestQty,
                request.instrumentId()
        );
    }

    @Override
    public RoundResultResponse endRound(Long userId, Long roomId, Long roundId) {
        Round round = getRoundByRoomAndRoundId(roomId, roundId);
        
        // 1. 해당 라운드의 모든 주문 잠금 (로그만 출력)
        List<Order> allRoundOrders = ordersRepository.findByRoundId(roundId);
        log.info("라운드 {} 주문이 잠겼습니다. 총 주문 수: {}", roundId, allRoundOrders.size());
        
        // 2. 해당 방의 모든 팀 조회
        List<Team> teams = teamRepository.findAllByRoomId(roomId);
        
        // 3. 각 팀별 투자 정보 계산
        List<RoundResultResponse.TeamInvestmentDto> teamInvestments = teams.stream()
                .map(team -> calculateTeamInvestmentInfo(team, round.getYear().getYearId()))
                .toList();
        
        return RoundResultResponse.builder()
                .roundNumber(round.getRoundNumber())
                .year(round.getYear().getYearId())
                .teamInvestments(teamInvestments)
                .build();
    }

    @Override
    public void endGame(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));

        room.setStatus(RoomStatus.ENDED);
        roomRepository.save(room);

    }

    // Entity 조회 메서드들

    private Round getRoundByRoomAndRoundId(Long roomId, Long roundId) {
        return roundRepository.findByRoomIdAndRoundId(roomId, roundId)
                .orElseThrow(() -> new RuntimeException("라운드를 찾을 수 없습니다."));
    }

    private Round getCurrentRoundByRoomId(Long roomId) {
        return roundRepository.findCurrentRoundByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("현재 라운드를 찾을 수 없습니다."));
    }

    private Team getUserTeam(Long userId, Long roomId) {
        return teamMemberRepository.findTeamByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("사용자의 팀을 찾을 수 없습니다."));
    }

    private Team getTeamForUpdate(Long teamId) {
        return teamRepository.findByIdForUpdate(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
    }

    private YearInstrument getYearInstrument(Long yearId, Long instrumentId) {
        return yearInstrumentRepository.findByYearIdAndInstrumentId(yearId, instrumentId)
                .orElseThrow(() -> new RuntimeException("해당 년도의 주식 정보를 찾을 수 없습니다."));
    }

    private StockHeld getHeldStock(Team teamForUpdate, YearInstrument yearInstrument) {
        return stockHeldRepository.findByTeamIdAndYearInstrumentIdForUpdate(
                        teamForUpdate.getId(), yearInstrument.getId())
                .orElseThrow(() -> new RuntimeException("보유하지 않은 주식입니다."));
    }

    // 거래 검증 및 처리 메서드들

    private void validateBuyRequest(int requestQty, Team team, int serverPrice) {
        if (requestQty <= 0) {
            throw new RuntimeException("수량은 1 이상이어야 합니다.");
        }
        
        int totalCost = serverPrice * requestQty;
        if (team.getAsset() < totalCost) {
            throw new RuntimeException("자산이 부족합니다.");
        }
    }

    private Order createAndSaveBuyOrder(Round round, Team team, int serverPrice, int requestQty) {
        Order order = Order.builder()
                .round(round)
                .team(team)
                .side(Side.BUY)
                .price(serverPrice)
                .qty(requestQty)
                .build();
        return ordersRepository.save(order);
    }

    private void updateStockPosition(Team team, YearInstrument yearInstrument, Order order, int requestQty) {
        StockHeld existingStock = stockHeldRepository.findByTeamIdAndYearInstrumentIdForUpdate(
                        team.getId(), yearInstrument.getId())
                .orElse(null);

        if (existingStock == null) {
            // 최초 생성
            StockHeld created = StockHeld.builder()
                    .order(order)
                    .yearInstrument(yearInstrument)
                    .team(team)
                    .qty(requestQty)
                    .build();
            stockHeldRepository.save(created);
        } else {
            // 기존 포지션 업데이트
            int newQty = existingStock.getQty() + requestQty;
            
            StockHeld updated = StockHeld.builder()
                    .id(existingStock.getId())
                    .order(existingStock.getOrder()) // 기존 참조 유지
                    .yearInstrument(yearInstrument)
                    .team(team)
                    .qty(newQty)
                    .build();
            stockHeldRepository.save(updated);
        }
    }

    // 응답 DTO 빌드 메서드

    private List<StockRoundDataResponse.StockInfoDto> buildStockInfoList(Long yearId) {
        return yearInstrumentRepository.findAllWithInstrumentByYearId(yearId).stream()
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

        int currentMoney = team.getAsset();
        int totalAsset = currentMoney + totalStockValue;

        return StockRoundDataResponse.TeamAssetDto.builder()
                .currentMoney(currentMoney)
                .totalAsset(totalAsset)
                .heldStocks(heldStockDtos)
                .build();
    }

    // 매도 검증 및 처리 메서드들

    private void validateSellRequest(int requestQty, StockHeld heldStock) {
        if (requestQty <= 0) {
            throw new RuntimeException("수량은 1 이상이어야 합니다.");
        }
        
        if (heldStock.getQty() < requestQty) {
            throw new RuntimeException("보유 수량이 부족합니다.");
        }
    }

    private Order createAndSaveSellOrder(Round round, Team team, int serverPrice, int requestQty) {
        Order order = Order.builder()
                .round(round)
                .team(team)
                .side(Side.SELL)
                .price(serverPrice)
                .qty(requestQty)
                .build();
        return ordersRepository.save(order);
    }

    private void updateStockPositionForSell(StockHeld heldStock, int requestQty) {
        int newQty = heldStock.getQty() - requestQty;
        
        if (newQty == 0) {
            // 모든 주식을 매도한 경우 삭제
            stockHeldRepository.delete(heldStock);
        } else {
            // 수량 업데이트
            StockHeld updated = StockHeld.builder()
                    .id(heldStock.getId())
                    .order(heldStock.getOrder()) // 기존 참조 유지
                    .yearInstrument(heldStock.getYearInstrument())
                    .team(heldStock.getTeam())
                    .qty(newQty)
                    .build();
            stockHeldRepository.save(updated);
        }
    }

    // 자산 관리 메서드들

    private void debitTeamAsset(Team team, int amount) {
        if (team.getAsset() < amount) {
            throw new RuntimeException("자산이 부족합니다.");
        }
        team.setAsset(team.getAsset() - amount);
    }

    private void creditTeamAsset(Team team, int amount) {
        team.setAsset(team.getAsset() + amount);
    }

    // 라운드 종료 관련 메서드들

    private RoundResultResponse.TeamInvestmentDto calculateTeamInvestmentInfo(Team team, Long yearId) {
        // 1. 해당 팀의 보유 주식 조회
        List<StockHeld> heldStocks = stockHeldRepository.findByTeamId(team.getId());
        
        // 2. 총 투자 금액 계산 (현재 주식 가치 기준)
        int totalInvestmentAmount = heldStocks.stream()
                .mapToInt(sh -> sh.getQty() * sh.getYearInstrument().getPrice())
                .sum();
        
        // 3. 최대 투자 종목 찾기 (투자금액 기준)
        String maxInvestmentStock = heldStocks.stream()
                .filter(sh -> sh.getQty() > 0) // 보유량이 있는 것만
                .max(Comparator.comparingInt(sh -> sh.getQty() * sh.getYearInstrument().getPrice()))
                .map(sh -> sh.getYearInstrument().getInstrument().getUiLabel())
                .orElse("없음");
        
        return RoundResultResponse.TeamInvestmentDto.builder()
                .teamName(team.getTeamName())
                .maxInvestmentStock(maxInvestmentStock)
                .totalInvestmentAmount(totalInvestmentAmount)
                .build();
    }
}
