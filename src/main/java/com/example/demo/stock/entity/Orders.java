package com.example.demo.stock.entity;

import com.example.demo.global.BaseEntity;
import com.example.demo.room.entity.Team;
import com.example.demo.stock.entity.enums.Side;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated
    @Column(name = "side", nullable = false)
    private Side side; // "BUY" or "SELL"

    @Column
    private Integer price;

    @Column
    private Integer qty;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockHeld> stockHeldList;
}
