package com.cubingclub.ranking.Scrambler.DTO;

import com.cubingclub.ranking.Scrambler.Entity.Scramble;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrambleResponseDTO {
    private Integer idx;
    private Integer attemptIdx;
    private String name;
    private String cckId;
    private Integer roundIdx;
    private String compName;
    private String eventName;
    private String roundName;
    private String roundGroup;
    private String seq;
    private String scramble;
    private String svg;
    
    public ScrambleResponseDTO(Scramble entity){
        this.idx=entity.getIdx();
        this.roundIdx=entity.getRound().getIdx();
        this.compName=entity.getRound().getComp().getCompName();
        this.eventName=entity.getRound().getCubeEvent().getEventName();
        this.roundName=entity.getRound().getRoundName();
        this.roundGroup=entity.getRoundGroup();
        this.seq=entity.getSeq();
        this.scramble=entity.getScramble();
        this.svg=entity.getSvg();
    }
}