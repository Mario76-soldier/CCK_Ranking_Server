package com.cubingclub.ranking.Round.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table
public class RoundGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @ManyToOne
    @JoinColumn
    private Round round;
    @Column
    private String groupName;
    @Builder
    public RoundGroup(Integer idx, Round round, String groupName){
        this.idx=idx;
        this.round=round;
        this.groupName=groupName;
    }
}
