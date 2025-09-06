package com.example.demo.stock.service;

import com.example.demo.room.entity.Room;
import com.example.demo.room.entity.Team;
import com.example.demo.room.entity.TeamMember;
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

import static com.example.demo.stock.entity.enums.Side.BUY;
import static com.example.demo.stock.entity.enums.Side.SELL;

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
    public StockRoundDataResponse retrieveRoundDate(Long userId, Long roomId) {
        Round currentRound = getCurrentRoundByRoomId(roomId);
        Team userTeam = getUserTeam(userId, roomId);
        Year year = currentRound.getYear();

        List<StockRoundDataResponse.StockInfoDto> stocks = buildStockInfoList(year.getYearId());
        StockRoundDataResponse.TeamAssetDto teamAsset = buildTeamAssetInfo(userTeam);

        return StockRoundDataResponse.builder()
                .teamId(userTeam.getId())
                .roundNumber(currentRound.getRoundNumber())
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
        validateManageStock(userId, roomId);

        Round currentRound = getCurrentRoundByRoomId(roomId);
        Team userTeam = getUserTeam(userId, roomId);
        YearInstrument yearInstrument = getYearInstrument(currentRound.getYear().getYearId(), request.instrumentId());
        
        Long serverPrice = yearInstrument.getYearOpenPrice();
        int requestQty = request.qty();
        
        validateBuyRequest(requestQty, userTeam, serverPrice);
        
        Order order = createAndSaveOrder(currentRound, userTeam, serverPrice, requestQty, BUY);
        
        debitTeamAsset(userTeam, serverPrice * requestQty);
        updateStockPositionForBuy(userTeam, yearInstrument, order, requestQty);
        
        stockWebSocketService.broadcastOrderExecution(order, userTeam, roomId, yearInstrument);

        return OrderResponse.of(
                order, userTeam, BUY,
                serverPrice, requestQty,
                request.instrumentId()
        );
    }

    @Override
    public OrderResponse sellStock(Long userId, Long roomId, OrderSellRequest request) {
        validateManageStock(userId, roomId);

        Round currentRound = getCurrentRoundByRoomId(roomId);
        Team userTeam = getUserTeam(userId, roomId);
        YearInstrument yearInstrument = getYearInstrument(currentRound.getYear().getYearId(), request.instrumentId());

        StockHeld heldStock = getHeldStock(userTeam, yearInstrument);
        
        Long serverPrice = yearInstrument.getYearOpenPrice();
        int requestQty = request.qty();
        
        validateSellRequest(requestQty, heldStock);
        
        Order order = createAndSaveOrder(currentRound, userTeam, serverPrice, requestQty, SELL);
        
        creditTeamAsset(userTeam, serverPrice * requestQty);
        updateStockPositionForSell(heldStock, requestQty);
        
        stockWebSocketService.broadcastOrderExecution(order, userTeam, roomId, yearInstrument);

        return OrderResponse.of(
                order, userTeam, Side.SELL,
                serverPrice, requestQty,
                request.instrumentId()
        );
    }

    @Override
    public RoundResultResponse endRound(Long userId, Long roomId) {
        Round currentRound = getCurrentRoundByRoomId(roomId);

        // Lock: 현재 라운드, 팀, 주식 보유 현황 등 잠금 조회
        List<Team> teams = teamRepository.findAllByRoomId(roomId);
        
        // 각 팀별 투자 정보 계산
        List<RoundResultResponse.TeamInvestmentDto> teamInvestments = teams.stream()
                .map(team -> calculateTeamInvestmentInfo(team, currentRound.getYear().getYearId()))
                .toList();

        // TODO: 라운드 종료 시 보여줄 정보 저장 후 스냅샷 테이블 저장
        // TODO: 마지막 라운드가 아니라면 다음 라운드로 넘어가는 로직 추가 (현재 라운드 상태 변경 및 다음 라운드 생성)

        return RoundResultResponse.builder()
                .roundNumber(currentRound.getRoundNumber())
                .year(currentRound.getYear().getYearId())
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

    private Round getCurrentRoundByRoomId(Long roomId) {
        return roundRepository.findCurrentRoundByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("현재 라운드를 찾을 수 없습니다."));
    }

    private Team getUserTeam(Long userId, Long roomId) {
        return teamMemberRepository.findTeamByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("사용자의 팀을 찾을 수 없습니다."));
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

    private void validateBuyRequest(int requestQty, Team team, Long price) {
        if (requestQty <= 0) {
            throw new RuntimeException("수량은 1 이상이어야 합니다.");
        }
        
        Long totalCost = price * requestQty;
        if (team.getAsset() < totalCost) {
            throw new RuntimeException("자산이 부족합니다.");
        }
    }

    private void validateSellRequest(int requestQty, StockHeld heldStock) {
        if (requestQty <= 0) {
            throw new RuntimeException("수량은 1 이상이어야 합니다.");
        }

        if (heldStock.getQty() < requestQty) {
            throw new RuntimeException("보유 수량이 부족합니다.");
        }
    }

    private void validateManageStock(Long userId, Long roomId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("사용자의 팀 멤버 정보를 찾을 수 없습니다."));

        if (!teamMember.getIsLeader()) {
            throw new RuntimeException("팀장만 주식 거래를 할 수 있습니다.");
        }
    }

    private Order createAndSaveOrder(Round round, Team team, Long price, int requestQty, Side side) {
        Order order = Order.builder()
                .round(round)
                .team(team)
                .side(side)
                .price(price)
                .qty(requestQty)
                .build();
        return ordersRepository.save(order);
    }

    private void updateStockPositionForBuy(Team team, YearInstrument yearInstrument, Order order, int requestQty) {
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

    private List<StockRoundDataResponse.StockInfoDto> buildStockInfoList(Long yearId) {
        return yearInstrumentRepository.findAllWithInstrumentByYearId(yearId).stream()
                .map(yi -> StockRoundDataResponse.StockInfoDto.builder()
                        .instrumentId(yi.getInstrument().getId())
                        .uiLabel(yi.getInstrument().getUiLabel())
                        .price(yi.getYearOpenPrice())
                        .build())
                .toList();
    }

    private StockRoundDataResponse.TeamAssetDto buildTeamAssetInfo(Team team) {
        List<StockHeld> heldStocks = stockHeldRepository.findByTeamId(team.getId());

        Long totalStockValue = heldStocks.stream()
                .mapToLong(sh -> sh.getQty() * sh.getYearInstrument().getYearOpenPrice())
                .sum();

        List<StockRoundDataResponse.HeldStockDto> heldStockDtos = heldStocks.stream()
                .map(sh -> StockRoundDataResponse.HeldStockDto.builder()
                        .instrumentId(sh.getYearInstrument().getInstrument().getId())
                        .affiliate(sh.getYearInstrument().getInstrument().getAffiliate())
                        .uiLabel(sh.getYearInstrument().getInstrument().getUiLabel())
                        .qty(sh.getQty())
                        .currentPrice(sh.getYearInstrument().getYearOpenPrice())
                        .totalValue(sh.getQty() * sh.getYearInstrument().getYearOpenPrice())
                        .build())
                .toList();

        Long currentMoney = team.getAsset();
        Long totalAsset = currentMoney + totalStockValue;

        return StockRoundDataResponse.TeamAssetDto.builder()
                .currentMoney(currentMoney)
                .totalAsset(totalAsset)
                .heldStocks(heldStockDtos)
                .build();
    }

    private void debitTeamAsset(Team team, Long amount) {
        if (team.getAsset() < amount) {
            throw new RuntimeException("자산이 부족합니다.");
        }
        team.setAsset(team.getAsset() - amount);
    }

    private void creditTeamAsset(Team team, Long amount) {
        team.setAsset(team.getAsset() + amount);
    }

    private RoundResultResponse.TeamInvestmentDto calculateTeamInvestmentInfo(Team team, Long yearId) {
        List<StockHeld> heldStocks = stockHeldRepository.findByTeamId(team.getId());
        
        Long totalInvestmentAmount = heldStocks.stream()
                .mapToLong(sh -> sh.getQty() * sh.getYearInstrument().getYearOpenPrice())
                .sum();
        
        String maxInvestmentStock = heldStocks.stream()
                .filter(sh -> sh.getQty() > 0) // 보유량이 있는 것만
                .max(Comparator.comparingLong(sh -> sh.getQty() * sh.getYearInstrument().getYearOpenPrice()))
                .map(sh -> sh.getYearInstrument().getInstrument().getUiLabel())
                .orElse("없음");
        
        return RoundResultResponse.TeamInvestmentDto.builder()
                .teamName(team.getTeamName())
                .maxInvestmentStock(maxInvestmentStock)
                .totalInvestmentAmount(totalInvestmentAmount)
                .build();
    }
}
