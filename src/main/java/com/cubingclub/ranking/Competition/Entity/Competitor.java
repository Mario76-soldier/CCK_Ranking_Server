package com.cubingclub.ranking.Competition.Entity;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class Competitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @ManyToOne
    @JoinColumn
    private Account account;
    @ManyToOne
    @JoinColumn
    private Competition competition;
    @Column
    private QueueStatus status;
    @Column
    private Integer waitingOrder;
    @Column
    private LocalDateTime timeStamp;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "competitor_idx", referencedColumnName = "idx"),
            inverseJoinColumns = @JoinColumn(name = "cubeEvent_eventName", referencedColumnName = "eventName")
    )
    private List<CubeEvent> eventList;
    @Column(nullable = false)
    private boolean attended;
}
