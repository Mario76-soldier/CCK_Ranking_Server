package com.cubingclub.ranking.Scrambler.Entity;

import com.cubingclub.ranking.Round.Entity.Round;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table
public class Scrambler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(
                    name = "fk_scrambler_round",
                    foreignKeyDefinition = "FOREIGN KEY (round_id) REFERENCES round(id) ON DELETE CASCADE"
            )
    )
    private Round round;
    @Column
    private String roundGroup;
    @Column
    private String seq;
    @Column
    private String scramble;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String svg;
    @Builder
    public Scrambler(Integer Idx, Round round, String roundGroup, String seq, String scramble, String svg){
        this.idx=Idx;
        this.round=round;
        this.roundGroup=roundGroup;
        this.seq=seq;
        this.scramble=scramble;
        this.svg=svg;
    }
}
