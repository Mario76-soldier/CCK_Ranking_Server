package com.cubingclub.ranking.Competition.DTO;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Entity.Competitor;
import com.cubingclub.ranking.Competition.Entity.QueueStatus;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompetitorDTO {
    private Integer idx;
    private AccountDTO account;
    private Competition competition;
    private QueueStatus status;
    private Integer waitingOrder;
    private LocalDateTime timeStamp;
    private List<CubeEvent> eventList;
    private boolean attended;
    public CompetitorDTO(Competitor entity){
        this.idx=entity.getIdx();
        this.account=new AccountDTO(entity.getAccount());
        this.competition=entity.getCompetition();
        this.status=entity.getStatus();
        this.waitingOrder=entity.getWaitingOrder();
        this.timeStamp=entity.getTimeStamp();
        this.eventList=entity.getEventList();
        this.attended=entity.isAttended();
    }
    public Competitor toEntity(AccountRepository accountRepository){
        return Competitor.builder()
                .idx(idx)
                .account(accountRepository.findByEmail(account.getEmail()).orElseThrow())
                .competition(competition)
                .status(status)
                .waitingOrder(waitingOrder)
                .timeStamp(timeStamp)
                .eventList(eventList)
                .attended(attended)
                .build();
    }
}
