package com.cubingclub.ranking.Competition.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table
public class WaitingQueue {
    @Id
    @Column(length=30)
    private String email;
    @Column
    private Integer compIdx;
    @Builder
    public WaitingQueue(String email, Integer compIdx){
        this.email=email;
        this.compIdx=compIdx;
    }
}
