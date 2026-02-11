package com.cubingclub.ranking.Round.Entity;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Round.DTO.RoundUpdateDTO;
import com.cubingclub.ranking.Scrambler.Entity.Scramble;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Competition comp;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CubeEvent cubeEvent;
    @Column(length=30, nullable = false)
    private String roundName;
    @Column(nullable = false)
    private LocalDateTime eventStart;
    @Column(nullable = false)
    private LocalDateTime eventEnd;
    @ElementCollection
    @BatchSize(size = 100)
    private final List<String> groupList = new ArrayList<>();
    @Column(precision = 10, scale=2)
    private BigDecimal cutoff;
    @Column(precision = 10, scale=2)
    private BigDecimal timeLimit;
    private Integer advance;
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore
    private final List<Record> records = new ArrayList<>();
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore
    private final List<Scramble> scrambles = new ArrayList<>();

    @Builder
    public Round(Integer idx, Competition comp, CubeEvent cubeEvent, String roundName,
                 LocalDateTime eventStart, LocalDateTime eventEnd,
                 BigDecimal cutoff, BigDecimal timeLimit, Integer advance){
        this.idx=idx;
        this.comp=comp;
        this.cubeEvent=cubeEvent;
        this.roundName=roundName;
        this.eventStart=eventStart;
        this.eventEnd=eventEnd;
        this.cutoff=cutoff;
        this.timeLimit=timeLimit;
        this.advance=advance;
    }
    public void update(RoundUpdateDTO roundUpdateDTO, CubeEvent cubeEvent) {
        this.cubeEvent = cubeEvent;
        this.roundName = roundUpdateDTO.getRoundName();
        this.eventStart = roundUpdateDTO.getEventStart();
        this.eventEnd = roundUpdateDTO.getEventEnd();
        this.cutoff= roundUpdateDTO.getCutoff();
        this.timeLimit= roundUpdateDTO.getTimeLimit();
        this.advance = roundUpdateDTO.getAdvance();
    }

    public void updateRoundGroups(List<String> newGroups) {
        this.groupList.clear();
        this.groupList.addAll(newGroups);
    }

    public void updateAdvance(Integer advance){
        this.advance=advance;
    }
}
