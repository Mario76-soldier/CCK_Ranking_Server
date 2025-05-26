package com.cubingclub.ranking.Scrambler.DTO;

import com.cubingclub.ranking.Account.Entity.Account;
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
public class ScramblerResponseDTO {
    private Integer idx;
    private Integer attemptIdx;
    private Account account;
    private Round round;
    private String roundGroup;
    private String seq;
    private String scramble;
    private String svg;
    public ScramblerResponseDTO(Scrambler entity){
        this.idx=entity.getIdx();
        this.round=entity.getRound();
        this.roundGroup=entity.getRoundGroup();
        this.seq=entity.getSeq();
        this.scramble=entity.getScramble();
        this.svg=entity.getSvg();
    }
}
