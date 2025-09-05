package com.example.demo.stock.entity;

import com.example.demo.room.entity.Room;
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
@Table(name = "round")
public class Round {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "round_id")
    private Long id;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @ManyToOne
    @JoinColumn(name = "year_id", nullable = false)
    private Year year;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamHistory> teamRounds;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orders> stockTransactions;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

}
