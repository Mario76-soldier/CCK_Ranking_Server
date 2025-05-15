package com.cubingclub.ranking.Scrambler.DTO;

import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Scrambler.Entity.Scrambler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScramblerDTO {
    private Integer idx;
    private Round round;
    private String roundGroup;
    private String seq;
    private String scramble;
    private String svg;
    public ScramblerDTO(Scrambler entity){
        this.idx=entity.getIdx();
        this.round=entity.getRound();
        this.roundGroup=entity.getRoundGroup();
        this.seq=entity.getSeq();
        this.scramble=entity.getScramble();
        this.svg=entity.getSvg();
    }
    public Scrambler toEntity(){
        return Scrambler.builder()
                .Idx(idx)
                .round(round)
                .roundGroup(roundGroup)
                .seq(seq)
                .scramble(scramble)
                .svg(svg)
                .build();
    }
}
