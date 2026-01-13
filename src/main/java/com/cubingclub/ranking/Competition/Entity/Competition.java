package com.cubingclub.ranking.Competition.Entity;

import com.cubingclub.ranking.Competition.DTO.CompetitionUpdateDTO;
import com.cubingclub.ranking.Round.Entity.Round;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    @Column(length=50, nullable = false)
    private String compName;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate compDateStart;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate compDateEnd;
    @Column(nullable = false)
    private LocalDateTime joinDateStart;
    @Column(nullable = false)
    private LocalDateTime joinDateEnd;
    private Integer baseFee;
    private String location;
    private Integer maxParticipate;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "comp", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private final List<EventFee> eventFees = new ArrayList<>();
    @OneToMany(mappedBy = "comp", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore
    private final List<Round> rounds = new ArrayList<>();
    @Builder
    public Competition(Integer Idx, String CompName, LocalDate CompDateStart, LocalDate CompDateEnd, LocalDateTime JoinDateStart, LocalDateTime JoinDateEnd, Integer baseFee, String location, Integer MaxParticipate, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.idx=Idx;
        this.compName=CompName;
        this.compDateStart=CompDateStart;
        this.compDateEnd=CompDateEnd;
        this.joinDateStart=JoinDateStart;
        this.joinDateEnd=JoinDateEnd;
        this.baseFee=baseFee;
        this.location=location;
        this.maxParticipate=MaxParticipate;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
    }

    public void update(CompetitionUpdateDTO competitionDTO){
        this.compName=competitionDTO.getCompName();
        this.compDateStart = competitionDTO.getCompDateStart();
        this.compDateEnd = competitionDTO.getCompDateEnd();
        this.joinDateStart = competitionDTO.getJoinDateStart();
        this.joinDateEnd = competitionDTO.getJoinDateEnd();
        this.location=competitionDTO.getLocation();
        this.baseFee=competitionDTO.getBaseFee();
        this.maxParticipate = competitionDTO.getMaxParticipate();
        this.updatedAt=LocalDateTime.now();
    }
}
