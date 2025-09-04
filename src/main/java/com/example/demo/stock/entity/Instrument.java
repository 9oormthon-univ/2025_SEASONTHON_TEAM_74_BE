package com.example.demo.stock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "instrument")
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instrument_id")
    private Long id;

    @Column
    private String name;

    @Column
    private String symbol;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "exchange_code")
    private String exchangeCode;

    @ManyToOne
    @JoinColumn(name = "industry_id")
    private Industry industry;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceYear> priceYearList = new ArrayList<>();

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScenarioInstrument> scenarioInstrumentList = new ArrayList<>();






}
