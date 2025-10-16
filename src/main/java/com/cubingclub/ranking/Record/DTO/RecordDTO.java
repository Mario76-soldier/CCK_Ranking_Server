package com.cubingclub.ranking.Record.DTO;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.DTO.MaskedAccountDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.BestType;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class RecordDTO {
    private Integer idx;
    private Integer pdx;
    private MaskedAccountDTO account;
    private Round round;
    private String roundGroup;
    private List<AttemptDTO> attempts;
    private BigDecimal single;
    private BigDecimal average;
    private Integer ranking;
    private Integer stateRanking;
    private BestType singleBestType;
    private BestType averageBestType;
    private String note;
    public RecordDTO(Record entity){
        this.idx=entity.getIdx();
        this.pdx=entity.getPdx();
        this.account=new MaskedAccountDTO(entity.getAccount());
        this.round=entity.getRound();
        this.roundGroup=entity.getRoundGroup();
        this.attempts=new ArrayList<>();
        for(Attempt attempt:entity.getAttempts()){
            this.attempts.add(new AttemptDTO(attempt));
        }
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.ranking=entity.getRanking();
        this.stateRanking=entity.getStateRanking();
        this.singleBestType=entity.getSingleBestType();
        this.averageBestType=entity.getAverageBestType();
        this.note=entity.getNote();
    }
    public RecordDTO(Account account, Integer pdx, Round round, String roundGroup, String note) {
        this.account=new MaskedAccountDTO(account);
        this.pdx=pdx;
        this.round=round;
        this.roundGroup=roundGroup;
        this.note=note;
    }
    public Record toEntity(AccountRepository accountRepository){
        return Record.builder()
                .pdx(pdx)
                .account(accountRepository.findByCckId(account.getCckId()).orElse(null))
                .round(round)
                .roundGroup(roundGroup)
                .note(note)
                .build();
    }
    public Record toEntityFull(AccountRepository accountRepository, List<Attempt> attempts) {
        return Record.builder()
                .idx(idx)
                .pdx(pdx)
                .account(accountRepository.findByCckId(account.getCckId()).orElse(null))
                .round(round)
                .roundGroup(roundGroup)
                .attempts(attempts)
                .single(single != null ? single : BigDecimal.ZERO)
                .average(average != null ? average : BigDecimal.ZERO)
                .ranking(ranking)
                .stateRanking(stateRanking)
                .singleBestType(singleBestType)
                .averageBestType(averageBestType)
                .note(note)
                .build();
    }

}
