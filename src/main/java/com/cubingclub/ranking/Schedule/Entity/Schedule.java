package com.cubingclub.ranking.Schedule.Entity;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Round.Entity.RoundGroup;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @ManyToOne
    @JoinColumn
    private Competition competition;
    @ManyToOne
    @JoinColumn
    private Account account;
    @ManyToOne
    @JoinColumn
    private RoundGroup roundGroup;
    @Column
    @Enumerated(EnumType.STRING)
    private PlayType playType;
    @Builder
    public Schedule(Integer idx, Competition competition, Account account, RoundGroup roundGroup, PlayType playType){
        this.idx=idx;
        this.competition=competition;
        this.account=account;
        this.roundGroup=roundGroup;
        this.playType=playType;
    }
}
