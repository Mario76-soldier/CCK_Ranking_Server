package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.Account;
import com.cubingclub.ranking.Entity.BestType;
import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Entity.Round;
import com.cubingclub.ranking.Repository.AccountRepository;
import jakarta.persistence.Column;
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
    private AccountDTO account;
    private Round round;
    private BigDecimal first;
    private String firstJudge;
    private String firstScrambler;
    private BigDecimal second;
    private String secondJudge;
    private String secondScrambler;
    private BigDecimal third;
    private String thirdJudge;
    private String thirdScrambler;
    private BigDecimal fourth;
    private String fourthJudge;
    private String fourthScrambler;
    private BigDecimal fifth;
    private String fifthJudge;
    private String fifthScrambler;
    private Integer extra1;
    private String extra1Reason;
    private Integer extra2;
    private String extra2Reason;
    private BigDecimal single;
    private BigDecimal average;
    private Integer ranking;
    private Integer stateRanking;
    private BestType singleBestType;
    private BestType averageBestType;
    private String checker1;
    private String checker2;
    public ParticipateDTO(Participate entity){
        this.idx=entity.getIdx();
        this.pdx=entity.getPdx();
        this.account=new AccountDTO(entity.getAccount());
        this.round=entity.getRound();
        this.first=entity.getFirst();
        this.second=entity.getSecond();
        this.third=entity.getThird();
        this.fourth=entity.getFourth();
        this.fifth=entity.getFifth();
        this.firstJudge=entity.getFirstJudge();
        this.secondJudge=entity.getSecondJudge();
        this.thirdJudge=entity.getThirdJudge();
        this.fourthJudge=entity.getFourthJudge();
        this.fifthJudge=entity.getFifthJudge();
        this.firstScrambler=entity.getFirstScrambler();
        this.secondScrambler=entity.getSecondScrambler();
        this.thirdScrambler=entity.getThirdScrambler();
        this.fourthScrambler=entity.getFourthScrambler();
        this.fifthScrambler=entity.getFifthScrambler();
        this.extra1=entity.getExtra1();
        this.extra1Reason=entity.getExtra1Reason();
        this.extra2=entity.getExtra2();
        this.extra2Reason=entity.getExtra2Reason();
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.ranking=entity.getRanking();
        this.stateRanking=entity.getStateRanking();
        this.singleBestType=entity.getSingleBestType();
        this.averageBestType=entity.getAverageBestType();
        this.checker1=entity.getChecker1();
        this.checker2=entity.getChecker2();
    }
    public ParticipateDTO(Account account, Integer pdx, Round round) {
        this.account=new AccountDTO(account);
        this.pdx=pdx;
        this.round=round;
    }
    public Participate toEntity(AccountRepository accountRepository){
        return Participate.builder()
                .Pdx(pdx)
                .account(accountRepository.findByEmail(account.getEmail()).orElse(null))
                .Round(round)
                .build();
    }
    public Participate toEntityFull(AccountRepository accountRepository){
        return Participate.builder()
                .Idx(idx)
                .Pdx(pdx)
                .account(accountRepository.findByEmail(account.getEmail()).orElse(null))
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
