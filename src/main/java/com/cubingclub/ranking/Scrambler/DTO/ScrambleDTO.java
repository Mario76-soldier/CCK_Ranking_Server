package com.cubingclub.ranking.Scrambler.DTO;

import com.cubingclub.ranking.Scrambler.Entity.Scramble;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScrambleDTO {
    String roundGroup;
    String seq;
    String scramble;
    String svg;

    public ScrambleDTO(Scramble entity){
        this.roundGroup=entity.getRoundGroup();
        this.seq=entity.getSeq();
        this.scramble=entity.getScramble();
        this.svg=entity.getSvg();
    }
}
