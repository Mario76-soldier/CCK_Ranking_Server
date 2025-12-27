package com.cubingclub.ranking.Record.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table
@Builder
public class Attempt {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Record record;
    @Column
    private Integer attemptTry;
    @Column(precision = 10, scale=2)
    private BigDecimal time;
    @Column
    private String scramblerName;
    @Column
    private String scramblerCckId;
    @Column
    private String judgeName;
    @Column
    private String judgeCckId;
    @Column
    private Integer extra;
    @Column
    private String exReason;
    @Column
    private String delegateName;
    @Column
    private String delegateCckId;
    @Column
    private String exScramblerName;
    @Column
    private String exScramblerCckId;
}
