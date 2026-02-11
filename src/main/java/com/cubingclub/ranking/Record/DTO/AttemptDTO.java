package com.cubingclub.ranking.Record.DTO;

import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDTO {
    private Integer idx;
    private Integer recordIdx;
    private Integer attemptTry;
    private BigDecimal time;
    private String scramblerName;
    private String scramblerCckId;
    private String judgeName;
    private String judgeCckId;
    private Integer extra;
    private String exReason;
    private String delegateName;
    private String delegateCckId;
    private String exScramblerName;
    private String exScramblerCckId;

    public AttemptDTO(Attempt entity) {
        this.idx = entity.getIdx();
        this.recordIdx=entity.getRecord().getIdx();
        this.attemptTry = entity.getAttemptTry();
        this.time = entity.getTime();
        this.scramblerName=entity.getScramblerName();
        this.scramblerCckId=entity.getScramblerCckId();
        this.judgeName=entity.getJudgeName();
        this.judgeCckId=entity.getJudgeCckId();
        this.extra = entity.getExtra();
        this.exReason = entity.getExReason();
        this.delegateName=entity.getDelegateName();
        this.delegateCckId=entity.getDelegateCckId();
        this.exScramblerName=entity.getExScramblerName();
        this.exScramblerCckId=entity.getExScramblerCckId();
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
    public Attempt toEntity(RecordRepository recordRepository) {
        return Attempt.builder()
                .idx(idx)
                .record(recordRepository.findByIdx(recordIdx))
                .attemptTry(attemptTry)
                .time(time)
                .scramblerName(scramblerName)
                .scramblerCckId(scramblerCckId)
                .judgeName(judgeName)
                .judgeCckId(judgeCckId)
                .extra(extra)
                .exReason(exReason)
                .delegateName(delegateName)
                .delegateCckId(delegateCckId)
                .exScramblerName(exScramblerName)
                .exScramblerCckId(exScramblerCckId)
                .build();
    }
}
