package com.cubingclub.ranking.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn
    private Account account;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn
    private CubeEvent cubeEvent;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal single;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal average;
    @Column
    private Integer ranking;
    @Column
    private Integer stateRanking;
}
