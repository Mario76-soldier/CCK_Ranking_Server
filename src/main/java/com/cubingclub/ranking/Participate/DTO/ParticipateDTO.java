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
    private String roundGroup;
    private BigDecimal first;
    private AccountDTO firstJudge;
    private AccountDTO firstScrambler;
    private BigDecimal second;
    private AccountDTO secondJudge;
    private AccountDTO secondScrambler;
    private BigDecimal third;
    private AccountDTO thirdJudge;
    private AccountDTO thirdScrambler;
    private BigDecimal fourth;
    private AccountDTO fourthJudge;
    private AccountDTO fourthScrambler;
    private BigDecimal fifth;
    private AccountDTO fifthJudge;
    private AccountDTO fifthScrambler;
    private Integer extra1;
    private String extra1Reason;
    private AccountDTO extra1Delegate;
    private Integer extra2;
    private String extra2Reason;
    private AccountDTO extra2Delegate;
    private BigDecimal single;
    private BigDecimal average;
    private Integer ranking;
    private Integer stateRanking;
    private BestType singleBestType;
    private BestType averageBestType;
    public ParticipateDTO(Participate entity){
        this.idx=entity.getIdx();
        this.pdx=entity.getPdx();
        this.account=new AccountDTO(entity.getAccount());
        this.round=entity.getRound();
        this.roundGroup=entity.getRoundGroup();
        this.first=entity.getFirst();
        this.second=entity.getSecond();
        this.third=entity.getThird();
        this.fourth=entity.getFourth();
        this.fifth=entity.getFifth();
        this.firstJudge = AccountDTO.from(entity.getFirstJudge()); // 변경
        this.secondJudge = AccountDTO.from(entity.getSecondJudge()); // 변경
        this.thirdJudge = AccountDTO.from(entity.getThirdJudge()); // 변경
        this.fourthJudge = AccountDTO.from(entity.getFourthJudge()); // 변경
        this.fifthJudge = AccountDTO.from(entity.getFifthJudge()); // 변경
        this.firstScrambler = AccountDTO.from(entity.getFirstScrambler()); // 변경
        this.secondScrambler = AccountDTO.from(entity.getSecondScrambler()); // 변경
        this.thirdScrambler = AccountDTO.from(entity.getThirdScrambler()); // 변경
        this.fourthScrambler = AccountDTO.from(entity.getFourthScrambler()); // 변경
        this.fifthScrambler = AccountDTO.from(entity.getFifthScrambler()); // 변경
        this.extra1=entity.getExtra1();
        this.extra1Reason=entity.getExtra1Reason();
        this.extra1Delegate=AccountDTO.from(entity.getExtra1Delegate());
        this.extra2=entity.getExtra2();
        this.extra2Reason=entity.getExtra2Reason();
        this.extra2Delegate=AccountDTO.from(entity.getExtra2Delegate());
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.ranking=entity.getRanking();
        this.stateRanking=entity.getStateRanking();
        this.singleBestType=entity.getSingleBestType();
        this.averageBestType=entity.getAverageBestType();
    }
    public ParticipateDTO(Account account, Integer pdx, Round round, String roundGroup) {
        this.account=new AccountDTO(account);
        this.pdx=pdx;
        this.round=round;
        this.roundGroup=roundGroup;
    }
    private boolean isNonZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) != 0;
    }
    public boolean allNonZero() {
        String avgCalc = round.getCubeEvent().getAvgCalc();
        return switch (avgCalc) {
            case "single" -> isNonZero(first);
            case "mo3" -> isNonZero(first)
                    && isNonZero(second)
                    && isNonZero(third);
            default -> isNonZero(first)
                    && isNonZero(second)
                    && isNonZero(third)
                    && isNonZero(fourth)
                    && isNonZero(fifth);
        };
    }
    public Participate toEntity(AccountRepository accountRepository){
        return Participate.builder()
                .pdx(pdx)
                .account(accountRepository.findByEmail(account.getEmail()).orElse(null))
                .round(round)
                .roundGroup(roundGroup)
                .build();
    }
    public Participate toEntityFull(AccountRepository accountRepository) {
        return Participate.builder()
                .idx(idx)
                .pdx(pdx)
                .account(accountRepository.findByEmail(account.getEmail()).orElse(null))
                .round(round)
                .roundGroup(roundGroup)
                .first(first != null ? first : BigDecimal.ZERO)
                .firstJudge(firstJudge != null ? accountRepository.findByEmail(firstJudge.getEmail()).orElse(null) : null)
                .firstScrambler(firstScrambler != null ? accountRepository.findByEmail(firstScrambler.getEmail()).orElse(null) : null)
                .second(second != null ? second : BigDecimal.ZERO)
                .secondJudge(secondJudge != null ? accountRepository.findByEmail(secondJudge.getEmail()).orElse(null) : null)
                .secondScrambler(secondScrambler != null ? accountRepository.findByEmail(secondScrambler.getEmail()).orElse(null) : null)
                .third(third != null ? third : BigDecimal.ZERO)
                .thirdJudge(thirdJudge != null ? accountRepository.findByEmail(thirdJudge.getEmail()).orElse(null) : null)
                .thirdScrambler(thirdScrambler != null ? accountRepository.findByEmail(thirdScrambler.getEmail()).orElse(null) : null)
                .fourth(fourth != null ? fourth : BigDecimal.ZERO)
                .fourthJudge(fourthJudge != null ? accountRepository.findByEmail(fourthJudge.getEmail()).orElse(null) : null)
                .fourthScrambler(fourthScrambler != null ? accountRepository.findByEmail(fourthScrambler.getEmail()).orElse(null) : null)
                .fifth(fifth != null ? fifth : BigDecimal.ZERO)
                .fifthJudge(fifthJudge != null ? accountRepository.findByEmail(fifthJudge.getEmail()).orElse(null) : null)
                .fifthScrambler(fifthScrambler != null ? accountRepository.findByEmail(fifthScrambler.getEmail()).orElse(null) : null)
                .extra1(extra1)
                .extra1Reason(extra1Reason)
                .extra1Delegate(extra1Delegate != null ? accountRepository.findByEmail(extra1Delegate.getEmail()).orElse(null) : null)
                .extra2(extra2)
                .extra2Reason(extra2Reason)
                .extra2Delegate(extra2Delegate != null ? accountRepository.findByEmail(extra2Delegate.getEmail()).orElse(null) : null)
                .single(single != null ? single : BigDecimal.ZERO)
                .average(average != null ? average : BigDecimal.ZERO)
                .ranking(ranking)
                .stateRanking(stateRanking)
                .singleBestType(singleBestType)
                .averageBestType(averageBestType)
                .build();
    }

}
