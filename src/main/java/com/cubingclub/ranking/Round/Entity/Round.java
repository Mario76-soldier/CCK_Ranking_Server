package com.cubingclub.ranking.Round.Entity;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Round.DTO.CreateRoundDTO;
import com.cubingclub.ranking.Round.Repository.RoundGroupRepository;
import com.cubingclub.ranking.Scrambler.Entity.Scrambler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    @Column(precision = 10, scale=2)
    private BigDecimal cutoff;
    @Column(precision = 10, scale=2)
    private BigDecimal timeLimit;
    @Column
    private Integer advance;
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Record> records = new ArrayList<>();
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Scrambler> scramblers = new ArrayList<>();
    @Builder
    public Round(Integer Idx, Competition Comp, CubeEvent cubeEvent, String RoundName,
                 LocalDateTime EventStart, LocalDateTime EventEnd, List<RoundGroup> roundGroup,
                 BigDecimal cutoff, BigDecimal timeLimit, Integer Advance){
        this.idx=Idx;
        this.comp=Comp;
        this.cubeEvent=cubeEvent;
        this.roundName=RoundName;
        this.eventStart=EventStart;
        this.eventEnd=EventEnd;
        this.roundGroup=roundGroup;
        this.cutoff=cutoff;
        this.timeLimit=timeLimit;
        this.advance=Advance;
    }
    public void update(CreateRoundDTO createRoundDTO, CubeEventRepository cubeEventRepository) {
        // competition은 절대 건드리지 않음
        this.cubeEvent = cubeEventRepository.findByEventName(createRoundDTO.getCubeEvent());
        this.roundName = createRoundDTO.getRoundName();
        this.eventStart = createRoundDTO.getEventStart();
        this.eventEnd = createRoundDTO.getEventEnd();
        this.cutoff=createRoundDTO.getCutoff();
        this.timeLimit=createRoundDTO.getTimeLimit();
        this.advance = createRoundDTO.getAdvance();

        // RoundGroup 처리
        List<String> newGroups = createRoundDTO.getGroupList();

        // 기존 그룹 삭제
        this.roundGroup.removeIf(rg -> !newGroups.contains(rg.getGroupName()));

        // 새로운 그룹 추가
        for (String groupName : newGroups) {
            boolean exists = this.roundGroup.stream()
                    .anyMatch(rg -> rg.getGroupName().equals(groupName));
            if (!exists) {
                RoundGroup newRg = RoundGroup.builder()
                        .round(this)
                        .groupName(groupName)
                        .build();
                this.roundGroup.add(newRg);
            }
        }
    }
}
