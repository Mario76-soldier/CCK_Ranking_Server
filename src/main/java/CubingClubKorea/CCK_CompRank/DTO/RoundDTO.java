package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.Competition;
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
    private Competition comp;
    private CubeEvent cubeEvent;
    private String roundName;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private Integer advance;
    public RoundDTO(Round entity){
        this.idx=entity.getIdx();
        this.comp=entity.getComp();
        this.cubeEvent=entity.getCubeEvent();
        this.roundName=entity.getRoundName();
        this.eventStart=entity.getEventStart();
        this.eventEnd=entity.getEventEnd();
        this.advance=entity.getAdvance();
    }
    public RoundDTO(CreateRoundDTO roundDTO){
        this.idx=roundDTO.getIdx();
        this.roundName=roundDTO.getRoundName();
        this.eventStart=roundDTO.getEventStart();
        this.eventEnd=roundDTO.getEventEnd();
        this.advance=roundDTO.getAdvance();
    }
    public Round toEntity(){
        return Round.builder()
                .Comp(comp)
                .cubeEvent(cubeEvent)
                .RoundName(roundName)
                .EventStart(eventStart)
                .EventEnd(eventEnd)
                .Advance(advance)
                .build();
    }
}
