package com.cubingclub.ranking.Record.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table
@Builder
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @Setter
    @JoinColumn(nullable = false)
    private Record record;
    private Integer attemptTry;
    @Column(precision = 10, scale=2)
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

    public void updateTime(BigDecimal time){
        this.time=time;
    }

    public void updateScrambler(String scramblerName, String scramblerCckId){
        this.scramblerName=scramblerName;
        this.scramblerCckId=scramblerCckId;
    }

    public void updateJudge(String judgeName, String judgeCckId){
        this.judgeName=judgeName;
        this.judgeCckId=judgeCckId;
    }

    public void updateExtra(Integer extra, String exReason, String delegateName, String delegateCckId){
        this.extra=extra;
        this.exReason=exReason;
        this.delegateName=delegateName;
        this.delegateCckId=delegateCckId;
    }

    public void updateExScrambler(String exScramblerName, String exScramblerCckId){
        this.exScramblerName=exScramblerName;
        this.exScramblerCckId=exScramblerCckId;
    }
}
