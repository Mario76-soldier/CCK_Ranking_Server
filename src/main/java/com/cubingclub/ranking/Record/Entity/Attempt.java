package com.cubingclub.ranking.Record.Entity;


import com.cubingclub.ranking.Account.Entity.Account;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Account scrambler;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Account judge;
    @Column
    private Integer extra;
    @Column
    private String exReason;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Account delegate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Account exScrambler;
}
