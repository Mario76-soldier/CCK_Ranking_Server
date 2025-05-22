package com.cubingclub.ranking.Record.Entity;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Round.Entity.Round;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table
@Builder
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @Column
    private Integer pdx;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Account account;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Round round;
    @Column
    private String roundGroup;
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    @OrderBy("attemptTry ASC")
    private List<Attempt> attempts;
    @Column(precision = 10, scale=2)
    private BigDecimal single;
    @Column(precision = 10, scale=2)
    private BigDecimal average;
    @Column
    private Integer ranking;
    @Column
    private Integer stateRanking;
    @Column
    private BestType singleBestType;
    @Column
    private BestType averageBestType;
}
