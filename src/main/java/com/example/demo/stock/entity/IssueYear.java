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
@Table(name = "issue_year")
public class IssueYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_year_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scenario_year_id")
    private ScenarioYear scenarioYear;

    @OneToMany(mappedBy = "issueYear", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueEffectSector> issueEffectSectorList = new ArrayList<>();

    @Column
    private String title;

    @Column
    private String detail;
}
