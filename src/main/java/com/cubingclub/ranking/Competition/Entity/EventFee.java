package com.cubingclub.ranking.Competition.Entity;

import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class EventFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @JsonIgnore
    private Integer idx;
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Competition comp;
    @ManyToOne
    @JoinColumn
    private CubeEvent cubeEvent;
    @Column
    private Integer eventFee;
}
