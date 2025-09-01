package com.cubingclub.ranking.Competition.Entity;

import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Round.Entity.Round;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @Column(length=50)
    private String compName;
    @Column
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate compDateStart;
    @Column
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate compDateEnd;
    @Column
    private LocalDateTime joinDateStart;
    @Column
    private LocalDateTime joinDateEnd;
    @Column
    private Integer maxParticipate;
    @OneToMany(mappedBy = "comp", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Round> rounds = new ArrayList<>();
    @Builder
    public Competition(Integer Idx, String CompName, LocalDate CompDateStart, LocalDate CompDateEnd, LocalDateTime JoinDateStart, LocalDateTime JoinDateEnd, Integer MaxParticipate){
        this.idx=Idx;
        this.compName=CompName;
        this.compDateStart=CompDateStart;
        this.compDateEnd=CompDateEnd;
        this.joinDateStart=JoinDateStart;
        this.joinDateEnd=JoinDateEnd;
        this.maxParticipate=MaxParticipate;
    }

    public void update(CompetitionDTO competitionDTO){
        this.compName=competitionDTO.getCompName();
        this.compDateStart = competitionDTO.getCompDateStart();
        this.compDateEnd = competitionDTO.getCompDateEnd();
        this.joinDateStart = competitionDTO.getJoinDateStart();
        this.joinDateEnd = competitionDTO.getJoinDateEnd();
        this.maxParticipate = competitionDTO.getMaxParticipate();
    }
}
