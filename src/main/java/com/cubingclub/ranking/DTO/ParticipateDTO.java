package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Entity.Round;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
public class ParticipateDTO {
    private Integer idx;
    private Integer pdx;
    private String userName;
    private String enUserName;
    private String email;
    private Round round;
    private BigDecimal first;
    private String firstJudge;
    private BigDecimal second;
    private String secondJudge;
    private BigDecimal third;
    private String thirdJudge;
    private BigDecimal fourth;
    private String fourthJudge;
    private BigDecimal fifth;
    private String fifthJudge;
    private BigDecimal single;
    private BigDecimal average;
    private Integer ranking;
    private String checker1;
    private String checker2;
    public ParticipateDTO(Participate entity){
        this.idx=entity.getIdx();
        this.pdx=entity.getPdx();
        this.userName=entity.getUserName();
        this.enUserName=entity.getEnUserName();
        this.email=entity.getEmail();
        this.round=entity.getRound();
        this.first=entity.getFirst();
        this.second=entity.getSecond();
        this.third=entity.getThird();
        this.fourth=entity.getFourth();
        this.fifth=entity.getFifth();
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.ranking=entity.getRanking();
        this.checker1=entity.getChecker1();
        this.checker2=entity.getChecker2();
    }
    public ParticipateDTO(String userName, String enUserName, Integer pdx, String email, Round round) {
        this.userName=userName;
        this.enUserName=enUserName;
        this.pdx=pdx;
        this.email=email;
        this.round=round;
    }
    public Participate toEntity(){
        return Participate.builder()
                .UserName(userName)
                .enUserName(enUserName)
                .Pdx(pdx)
                .Email(email)
                .Round(round)
                .build();
    }
    public Participate toEntityFull(){
        return Participate.builder()
                .Idx(idx)
                .UserName(userName)
                .enUserName(enUserName)
                .Pdx(pdx)
                .Email(email)
                .Round(round)
                .first(first)
                .firstJudge(firstJudge)
                .second(second)
                .secondJudge(secondJudge)
                .third(third)
                .thirdJudge(thirdJudge)
                .fourth(fourth)
                .fourthJudge(fourthJudge)
                .fifth(fifth)
                .fifthJudge(fifthJudge)
                .build();
    }
}
