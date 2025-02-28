package com.cubingclub.ranking.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class BestRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @ManyToOne
    @JoinColumn
    private Account account;
    @Column
    private Integer partSingle;
    @Column
    private Integer partAverage;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn
    private CubeEvent cubeEvent;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal single;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal average;
    @Column
    private Long singleRanking;
    @Column
    private Long averageRanking;
    @Column
    private Long stateSingleRanking;
    @Column
    private Long stateAverageRanking;
}
