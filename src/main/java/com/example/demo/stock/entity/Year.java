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
@Table(name = "year")
public class Year {

    @Id
    @Column(name = "year_id", nullable = false)
    private Integer yearId;

    @Column(name = "hint1")
    private String hint1;

    @Column(name = "hint2")
    private String hint2;

    @Column(name = "hint3")
    private String hint3;

    @Column(name = "hint1_detail")
    private String hint1Detail;

    @Column(name = "hint2_detail")
    private String hint2Detail;

    @Column(name = "hint3_detail")
    private String hint3Detail;

    @OneToMany(mappedBy = "year", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YearInstrument> yearInstruments;

    @OneToMany(mappedBy = "year", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds;
}
