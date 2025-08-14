package com.cubingclub.ranking.Round.Entity;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Scrambler.Entity.Scrambler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Competition comp;
    @ManyToOne
    @JoinColumn
    private CubeEvent cubeEvent;
    @Column(length=30)
    private String roundName;
    @Column
    private LocalDateTime eventStart;
    @Column
    private LocalDateTime eventEnd;
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private List<RoundGroup> roundGroup;
    @Column
    private Integer advance;
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Record> records = new ArrayList<>();
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Scrambler> scramblers = new ArrayList<>();
    @Builder
    public Round(Integer Idx, Competition Comp, CubeEvent cubeEvent, String RoundName, LocalDateTime EventStart, LocalDateTime EventEnd, List<RoundGroup> roundGroup, Integer Advance){
        this.idx=Idx;
        this.comp=Comp;
        this.cubeEvent=cubeEvent;
        this.roundName=RoundName;
        this.eventStart=EventStart;
        this.eventEnd=EventEnd;
        this.roundGroup=roundGroup;
        this.advance=Advance;
    }
}
