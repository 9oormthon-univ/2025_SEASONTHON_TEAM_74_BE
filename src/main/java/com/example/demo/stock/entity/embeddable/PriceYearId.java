package com.example.demo.stock.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PriceYearId implements Serializable {
    @Column(name = "instrument_id", nullable = false)
    private Long instrumentId;

    @Column(name = "year", nullable = false)
    private Integer year;
}