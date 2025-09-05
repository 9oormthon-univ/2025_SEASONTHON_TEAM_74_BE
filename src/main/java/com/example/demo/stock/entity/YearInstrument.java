package com.example.demo.stock.entity;

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
@Table(name = "year_instrument")
public class YearInstrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "year_instrument_id")
    private Long id;

    @Column
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "year_id", nullable = false)
    private Year year;

    @ManyToOne
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @OneToMany(mappedBy = "yearInstrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockHeld> socksHeldList;
}
