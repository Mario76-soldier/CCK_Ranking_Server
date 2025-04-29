package com.cubingclub.ranking.Participate.Entity;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Round.Entity.Round;
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
public class Participate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @Column
    private Integer pdx;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account account;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Round round;
    @Column(precision = 10, scale=2)
    private BigDecimal first=BigDecimal.ZERO;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account firstJudge;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account firstScrambler;
    @Column(precision = 10, scale=2)
    private BigDecimal second=BigDecimal.ZERO;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account secondJudge;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account secondScrambler;
    @Column(precision = 10, scale=2)
    private BigDecimal third=BigDecimal.ZERO;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account thirdJudge;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account thirdScrambler;
    @Column(precision = 10, scale=2)
    private BigDecimal fourth=BigDecimal.ZERO;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account fourthJudge;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account fourthScrambler;
    @Column(precision = 10, scale=2)
    private BigDecimal fifth=BigDecimal.ZERO;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account fifthJudge;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account fifthScrambler;
    @Column
    private Integer extra1;
    @Column
    private String extra1Reason;
    @Column
    private Integer extra2;
    @Column
    private String extra2Reason;
    @Column(precision = 10, scale=2)
    private BigDecimal single=BigDecimal.ZERO;
    @Column(precision = 10, scale=2)
    private BigDecimal average=BigDecimal.ZERO;
    @Column
    private Integer ranking;
    @Column
    private Integer stateRanking;
    @Column
    private BestType singleBestType;
    @Column
    private BestType averageBestType;
    public Participate update(BigDecimal First, BigDecimal Second, BigDecimal Third, BigDecimal Fourth, BigDecimal Fifth, BigDecimal Single, BigDecimal Average, Integer Ranking, Integer StateRanking, BestType singleBestType, BestType averageBestType){
        this.first=First;
        this.second=Second;
        this.third=Third;
        this.fourth=Fourth;
        this.fifth=Fifth;
        this.single=Single;
        this.average=Average;
        this.ranking=Ranking;
        this.stateRanking=StateRanking;
        this.singleBestType=singleBestType;
        this.averageBestType=averageBestType;
        return this;
    }
}
