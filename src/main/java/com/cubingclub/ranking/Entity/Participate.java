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
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn
    private Round round;
    @Column(length=40)
    private String userName;
    @Column(length=40)
    private String enUserName;
    @Column(length=30)
    private String email;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal first;
    @Column(length=30)
    private String firstJudge;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal second;
    @Column(length=30)
    private String secondJudge;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal third;
    @Column(length=30)
    private String thirdJudge;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal fourth;
    @Column(length=30)
    private String fourthJudge;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal fifth;
    @Column(length=30)
    private String fifthJudge;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal single;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal average;
    @Column
    private Integer ranking;
    @Column(length=20)
    private String checker1;
    @Column(length=20)
    private String checker2;

    @Builder
    public Participate(Integer Idx, String UserName, String enUserName, Integer Pdx, String Email, Round Round, BigDecimal first,
                       String firstJudge, BigDecimal second, String secondJudge,
                       BigDecimal third, String thirdJudge, BigDecimal fourth, String fourthJudge,BigDecimal fifth, String fifthJudge){
        this.idx=Idx;
        this.userName=UserName;
        this.enUserName=enUserName;
        this.pdx=Pdx;
        this.email=Email;
        this.round=Round;
        this.first=first;
        this.firstJudge=firstJudge;
        this.second=second;
        this.secondJudge=secondJudge;
        this.third=third;
        this.thirdJudge=thirdJudge;
        this.fourth=fourth;
        this.fourthJudge=fourthJudge;
        this.fifth=fifth;
        this.fifthJudge=fifthJudge;
    }

    public Participate update(BigDecimal First, BigDecimal Second, BigDecimal Third, BigDecimal Fourth, BigDecimal Fifth, BigDecimal Single, BigDecimal Average, Integer Ranking, String Checker1){
        this.first=First;
        this.second=Second;
        this.third=Third;
        this.fourth=Fourth;
        this.fifth=Fifth;
        this.single=Single;
        this.average=Average;
        this.ranking=Ranking;
        this.checker1=Checker1;
        return this;
    }

    public Participate update(String Checker2){
        this.checker2=Checker2;
        return this;
    }

}
