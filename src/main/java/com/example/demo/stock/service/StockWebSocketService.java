package com.example.demo.stock.service;

import com.example.demo.room.entity.Team;
import com.example.demo.stock.dto.websocket.StockUpdateMessage;
import com.example.demo.stock.entity.Order;
import com.example.demo.stock.entity.StockHeld;
import com.example.demo.stock.entity.YearInstrument;
import com.example.demo.stock.repository.StockHeldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final StockHeldRepository stockHeldRepository;

    /**
     * 팀 자산 업데이트를 방의 모든 사용자에게 브로드캐스트
     * 라운드 종료 후 이자 지급, 배당금 지급 등에 사용
     *   teams.forEach(team -> {
     *       team.setAsset(team.getAsset() + interest);
     *       stockWebSocketService.broadcastTeamAssetUpdate(team, roomId);
     *   });
     */
    public void broadcastTeamAssetUpdate(Team team, Long roomId) {
        try {
            StockUpdateMessage.TeamAssetUpdate teamAssetUpdate = buildTeamAssetUpdate(team);
            
            StockUpdateMessage message = StockUpdateMessage.builder()
                    .type(StockUpdateMessage.UpdateType.TEAM_ASSET_UPDATE)
                    .roomId(roomId)
                    .teamId(team.getId())
                    .teamName(team.getTeamName())
                    .teamAsset(teamAssetUpdate)
                    .build();

            // 해당 방의 모든 사용자에게 브로드캐스트
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/stock-updates", message);
            
            log.debug("팀 자산 업데이트 브로드캐스트 완료 - 방: {}, 팀: {}", roomId, team.getId());
        } catch (Exception e) {
            log.error("팀 자산 업데이트 브로드캐스트 실패 - 방: {}, 팀: {}", roomId, team.getId(), e);
        }
    }

    /**
     * 주문 체결을 방의 모든 사용자에게 브로드캐스트
     */
    public void broadcastOrderExecution(Order order, Team team, Long roomId, YearInstrument yearInstrument) {
        try {
            StockUpdateMessage.OrderUpdate orderUpdate = StockUpdateMessage.OrderUpdate.builder()
                    .orderId(order.getId())
                    .side(order.getSide())
                    .instrumentId(yearInstrument.getInstrument().getId())
                    .instrumentName(yearInstrument.getInstrument().getUiLabel())
                    .price(order.getPrice())
                    .qty(order.getQty())
                    .timestamp(System.currentTimeMillis())
                    .build();

            StockUpdateMessage.TeamAssetUpdate teamAssetUpdate = buildTeamAssetUpdate(team);

            StockUpdateMessage message = StockUpdateMessage.builder()
                    .type(StockUpdateMessage.UpdateType.ORDER_EXECUTED)
                    .roomId(roomId)
                    .teamId(team.getId())
                    .teamName(team.getTeamName())
                    .teamAsset(teamAssetUpdate)
                    .orderUpdate(orderUpdate)
                    .build();

            // 해당 방의 모든 사용자에게 브로드캐스트
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/stock-updates", message);
            
            log.debug("주문 체결 브로드캐스트 완료 - 방: {}, 팀: {}, 주문: {}", roomId, team.getId(), order.getId());
        } catch (Exception e) {
            log.error("주문 체결 브로드캐스트 실패 - 방: {}, 팀: {}, 주문: {}", roomId, team.getId(), order.getId(), e);
        }
    }

    /**
     * 팀 자산 정보를 구성
     */
    private StockUpdateMessage.TeamAssetUpdate buildTeamAssetUpdate(Team team) {
        List<StockHeld> heldStocks = stockHeldRepository.findByTeamId(team.getId());

        int totalStockValue = heldStocks.stream()
                .mapToInt(sh -> sh.getQty() * sh.getYearInstrument().getPrice())
                .sum();

        List<StockUpdateMessage.HeldStockUpdate> heldStockUpdates = heldStocks.stream()
                .map(sh -> StockUpdateMessage.HeldStockUpdate.builder()
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

        return StockUpdateMessage.TeamAssetUpdate.builder()
                .currentMoney(currentMoney)
                .totalAsset(totalAsset)
                .heldStocks(heldStockUpdates)
                .build();
    }
}
