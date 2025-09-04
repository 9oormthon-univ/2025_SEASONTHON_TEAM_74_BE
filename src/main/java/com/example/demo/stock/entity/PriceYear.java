package com.example.demo.stock.entity;

import com.example.demo.stock.entity.embeddable.PriceYearId;
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
@Table(name = "price_year")
public class PriceYear {

    @EmbeddedId
    private PriceYearId priceYearId;

    @Column(name = "open_p")
    private String openP;

    @Column(name = "close_p")
    private String closeP;

    @Column(name = "high_p")
    private String highP;

    @Column(name = "low_p")
    private String lowP;

    @Column(name = "volume_p")
    private String volumeP;

    @Column(name = "rest_p")
    private String restP;

    @ManyToOne
    @MapsId("instrumentId")
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;


}
