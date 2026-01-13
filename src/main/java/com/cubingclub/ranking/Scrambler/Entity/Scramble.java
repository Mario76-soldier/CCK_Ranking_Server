package com.cubingclub.ranking.Scrambler.Entity;

import com.cubingclub.ranking.Round.Entity.Round;
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
public class Scramble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    @ManyToOne(fetch=FetchType.LAZY)
    private Round round;
    private String roundGroup;
    private String seq;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String scramble;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String svg;
}