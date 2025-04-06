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
    @ManyToOne
    @JoinColumn
    private Account account;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn
    private Round round;
    @Column(precision = 10, scale=2)
    private BigDecimal first=BigDecimal.ZERO;
    @Column(length=30)
    private String firstJudge;
    @Column(length=30)
    private String firstScrambler;
    @Column(precision = 10, scale=2)
    private BigDecimal second=BigDecimal.ZERO;
    @Column(length=30)
    private String secondJudge;
    @Column(length=30)
    private String secondScrambler;
    @Column(precision = 10, scale=2)
    private BigDecimal third=BigDecimal.ZERO;
    @Column(length=30)
    private String thirdJudge;
    @Column(length=30)
    private String thirdScrambler;
    @Column(precision = 10, scale=2)
    private BigDecimal fourth=BigDecimal.ZERO;
    @Column(length=30)
    private String fourthJudge;
    @Column(length=30)
    private String fourthScrambler;
    @Column(precision = 10, scale=2)
    private BigDecimal fifth=BigDecimal.ZERO;
    @Column(length=30)
    private String fifthJudge;
    @Column(length=30)
    private String fifthScrambler;
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
    @Column(length=20)
    private String checker1;
    @Column(length=20)
    private String checker2;
    public Participate update(BigDecimal First, BigDecimal Second, BigDecimal Third, BigDecimal Fourth, BigDecimal Fifth, BigDecimal Single, BigDecimal Average, Integer Ranking, Integer StateRanking, BestType singleBestType, BestType averageBestType, String Checker1){
        this.first=First;
        this.second=Second;
        this.third=Third;
        this.fourth=Fourth;
        this.fifth=Fifth;
        this.single=Single;
        this.average=Average;
        this.ranking=Ranking;
        this.stateRanking=StateRanking;
        this.checker1=Checker1;
        this.singleBestType=singleBestType;
        this.averageBestType=averageBestType;
        return this;
    }

    public Participate update(String Checker2){
        this.checker2=Checker2;
        return this;
    }

}
