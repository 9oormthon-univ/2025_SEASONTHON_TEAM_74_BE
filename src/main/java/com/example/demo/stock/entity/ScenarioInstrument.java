package com.example.demo.stock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "scenario_instrument")
public class ScenarioInstrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scenario_instrument_id")
    private Long id;

    @Column(name = "ui_label")
    private String uiLabel;

    @ManyToOne
    @JoinColumn(name = "scenario_year_id")
    private ScenarioYear scenarioYear;

    @ManyToOne
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;



}
