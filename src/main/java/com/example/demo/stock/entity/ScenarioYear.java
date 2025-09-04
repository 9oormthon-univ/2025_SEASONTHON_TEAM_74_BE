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
@Table(name = "scenario_year")
public class ScenarioYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scenario_year_id")
    private Long id;

    @Column
    private String year;

    @Column
    private String label;

    @Column
    private String tz;

    @OneToMany(mappedBy = "scenarioYear", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueYear> issueYearList = new ArrayList<>();

    @OneToMany(mappedBy = "scenarioYear", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScenarioInstrument> scenarioInstrumentList = new ArrayList<>();


}
