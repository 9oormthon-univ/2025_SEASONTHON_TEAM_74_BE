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
@Table(name = "industry")
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sector_id")
    private Long id;

    @Column
    private String name;

    @Column
    private String code;

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Industry> industryList = new ArrayList<>();

}
