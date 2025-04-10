package com.cubingclub.ranking.Participate.DTO;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Participate.Entity.BestType;
import com.cubingclub.ranking.Participate.Entity.Participate;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
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
    public boolean allNonZero() {
        String avgCalc = round.getCubeEvent().getAvgCalc();
        return switch (avgCalc) {
            case "single" -> !first.equals(BigDecimal.ZERO);
            case "mo3" -> !first.equals(BigDecimal.ZERO)
                    && !second.equals(BigDecimal.ZERO)
                    && !third.equals(BigDecimal.ZERO);
            default -> !first.equals(BigDecimal.ZERO)
                    && !second.equals(BigDecimal.ZERO)
                    && !third.equals(BigDecimal.ZERO)
                    && !fourth.equals(BigDecimal.ZERO)
                    && !fifth.equals(BigDecimal.ZERO);
        };
    }
    public Participate toEntity(AccountRepository accountRepository){
        return Participate.builder()
                .pdx(pdx)
                .account(accountRepository.findByEmail(account.getEmail()).orElse(null))
                .round(round)
                .build();
    }
    public Participate toEntityFull(AccountRepository accountRepository) {
        return Participate.builder()
                .idx(idx)
                .pdx(pdx)
                .account(accountRepository.findByEmail(account.getEmail()).orElse(null))
                .round(round)
                .first(first != null ? first : BigDecimal.ZERO)
                .firstJudge(firstJudge)
                .firstScrambler(firstScrambler)
                .second(second != null ? second : BigDecimal.ZERO)
                .secondJudge(secondJudge)
                .secondScrambler(secondScrambler)
                .third(third != null ? third : BigDecimal.ZERO)
                .thirdJudge(thirdJudge)
                .thirdScrambler(thirdScrambler)
                .fourth(fourth != null ? fourth : BigDecimal.ZERO)
                .fourthJudge(fourthJudge)
                .fourthScrambler(fourthScrambler)
                .fifth(fifth != null ? fifth : BigDecimal.ZERO)
                .fifthJudge(fifthJudge)
                .fifthScrambler(fifthScrambler)
                .extra1(extra1)
                .extra1Reason(extra1Reason)
                .extra2(extra2)
                .extra2Reason(extra2Reason)
                .single(single != null ? single : BigDecimal.ZERO)
                .average(average != null ? average : BigDecimal.ZERO)
                .ranking(ranking)
                .stateRanking(stateRanking)
                .singleBestType(singleBestType)
                .averageBestType(averageBestType)
                .checker1(checker1)
                .checker2(checker2)
                .build();
    }
}
