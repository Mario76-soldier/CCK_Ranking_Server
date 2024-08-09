package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.CubeEvent;
import CubingClubKorea.CCK_CompRank.Entity.Round;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RoundDTO {
    private Integer idx;
    private Integer compIdx;
    private CubeEvent cubeEvent;
    private String round;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private Integer advance;
    public RoundDTO(Round entity){
        this.idx=entity.getIdx();
        this.compIdx=entity.getCompIdx();
        this.cubeEvent=entity.getCubeEvent();
        this.round=entity.getRound();
        this.eventStart=entity.getEventStart();
        this.eventEnd=entity.getEventEnd();
        this.advance=entity.getAdvance();
    }
    public RoundDTO(CreateRoundDTO roundDTO){
        this.idx=roundDTO.getIdx();
        this.compIdx=roundDTO.getCompIdx();
        this.round=roundDTO.getRound();
        this.eventStart=roundDTO.getEventStart();
        this.eventEnd=roundDTO.getEventEnd();
        this.advance=roundDTO.getAdvance();
    }
    public Round toEntity(){
        return Round.builder()
                .CompIdx(compIdx)
                .cubeEvent(cubeEvent)
                .Round(round)
                .EventStart(eventStart)
                .EventEnd(eventEnd)
                .Advance(advance)
                .build();
    }
}
