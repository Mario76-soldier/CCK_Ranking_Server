package com.cubingclub.ranking.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table
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
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal first;
    @Column(length=30)
    private String firstJudge;
    @Column(length=30)
    private String firstScrambler;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal second;
    @Column(length=30)
    private String secondJudge;
    @Column(length=30)
    private String secondScrambler;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal third;
    @Column(length=30)
    private String thirdJudge;
    @Column(length=30)
    private String thirdScrambler;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal fourth;
    @Column(length=30)
    private String fourthJudge;
    @Column(length=30)
    private String fourthScrambler;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal fifth;
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
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal single;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal average;
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

    @Builder
    public Participate(Integer Idx, Integer Pdx, Account account, Round Round, BigDecimal first,
                       String firstJudge, String firstScrambler, BigDecimal second, String secondJudge, String secondScrambler,
                       BigDecimal third, String thirdJudge, String thirdScrambler, BigDecimal fourth, String fourthJudge, String fourthScrambler, BigDecimal fifth, String fifthJudge, String fifthScrambler){
        this.idx=Idx;
        this.pdx=Pdx;
        this.account=account;
        this.round=Round;
        this.first=first;
        this.firstJudge=firstJudge;
        this.firstScrambler=firstScrambler;
        this.second=second;
        this.secondJudge=secondJudge;
        this.secondScrambler=secondScrambler;
        this.third=third;
        this.thirdJudge=thirdJudge;
        this.thirdScrambler=thirdScrambler;
        this.fourth=fourth;
        this.fourthJudge=fourthJudge;
        this.fourthScrambler=fourthScrambler;
        this.fifth=fifth;
        this.fifthJudge=fifthJudge;
        this.fifthScrambler=fifthScrambler;
    }

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
