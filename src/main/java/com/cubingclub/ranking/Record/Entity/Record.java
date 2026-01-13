package com.cubingclub.ranking.Record.Entity;

import com.cubingclub.ranking.Round.Entity.Round;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    private Integer pdx;
    @Column(nullable = false)
    private String name;
    private String state;
    private String cckId;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Round round;
    @Column(length = 20)
    private String roundGroup;
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    @OrderBy("attemptTry ASC")
    private List<Attempt> attempts;
    @Column(precision = 10, scale=2)
    private BigDecimal single;
    @Column(precision = 10, scale=2)
    private BigDecimal average;
    private Integer ranking;
    private Integer stateRanking;
    private BestType singleBestType;
    private BestType averageBestType;
    private String note;
}
