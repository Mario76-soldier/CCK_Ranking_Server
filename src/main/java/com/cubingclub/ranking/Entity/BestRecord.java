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
    @ManyToOne
    @JoinColumn
    private CubeEvent cubeEvent;
    @OneToOne
    @JoinColumn
    private Participate single;
    @OneToOne
    @JoinColumn
    private Participate average;
    @Column
    private Long singleRanking;
    @Column
    private Long averageRanking;
    @Column
    private Long stateSingleRanking;
    @Column
    private Long stateAverageRanking;
}
