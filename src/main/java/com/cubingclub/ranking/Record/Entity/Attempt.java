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
}
