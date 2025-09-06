package com.example.demo.stock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "year_instrument")
public class YearInstrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "year_instrument_id")
    private Long id;

    @Column(name = "year_open_price", nullable = false)
    private Long yearOpenPrice;

    @Column(name= "year_close_price", nullable = false)
    private Long yearClosePrice;

    @Column(name = "annual_return_pct", precision = 6, scale = 2, nullable = false)
    private BigDecimal annualReturnPct;

    @ManyToOne
    @JoinColumn(name = "year_id", nullable = false)
    private Year year;

    @ManyToOne
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @OneToMany(mappedBy = "yearInstrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockHeld> socksHeldList;
}
