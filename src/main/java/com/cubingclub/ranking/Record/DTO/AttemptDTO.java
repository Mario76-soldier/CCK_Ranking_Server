package com.cubingclub.ranking.Record.DTO;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.DTO.MaskedAccountDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AttemptDTO {
    private Integer idx;
    private Integer recordIdx;
    private Integer attemptTry;
    private BigDecimal time;
    private MaskedAccountDTO scrambler;
    private MaskedAccountDTO judge;
    private Integer extra;
    private String exReason;
    private MaskedAccountDTO delegate;
    private MaskedAccountDTO exScrambler;
    public AttemptDTO(Attempt entity) {
        this.idx = entity.getIdx();
        this.recordIdx=entity.getRecord().getIdx();
        this.attemptTry = entity.getAttemptTry();
        this.time = entity.getTime();
        this.scrambler = entity.getScrambler() != null ? new MaskedAccountDTO(entity.getScrambler()) : null;
        this.judge = entity.getJudge() != null ? new MaskedAccountDTO(entity.getJudge()) : null;
        this.delegate = entity.getDelegate() != null ? new MaskedAccountDTO(entity.getDelegate()) : null;
        this.exScrambler = entity.getExScrambler() != null ? new MaskedAccountDTO(entity.getExScrambler()) : null;
        this.extra = entity.getExtra(); // isExtra
        this.exReason = entity.getExReason();
    }
    public AttemptDTO(Record record, Integer attemptTry){
        this.recordIdx=record.getIdx();
        this.attemptTry=attemptTry;
    }
    public Attempt toInitialEntity(RecordRepository recordRepository){
        return Attempt.builder()
                .record(recordRepository.findByIdx(recordIdx))
                .attemptTry(attemptTry)
                .build();
    }
    public Attempt toEntity(RecordRepository recordRepository, AccountRepository accountRepository) {
        return Attempt.builder()
                .idx(idx)
                .record(recordRepository.findByIdx(recordIdx))
                .attemptTry(attemptTry)
                .time(time)
                .scrambler(scrambler != null ? accountRepository.findByCckId(scrambler.getCckId()).orElse(null) : null)
                .judge(judge != null ? accountRepository.findByCckId(judge.getCckId()).orElse(null) : null)
                .extra(extra)
                .exReason(exReason)
                .delegate(delegate != null ? accountRepository.findByCckId(delegate.getCckId()).orElse(null) : null)
                .exScrambler(exScrambler != null ? accountRepository.findByCckId(exScrambler.getCckId()).orElse(null) : null)
                .build();
    }
}
