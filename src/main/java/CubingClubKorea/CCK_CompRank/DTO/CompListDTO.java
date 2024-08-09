package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.CompList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CompListDTO {
    private Integer idx;
    private String compName;
    private LocalDate compDateStart;
    private LocalDate compDateEnd;
    private LocalDate joinDateStart;
    private LocalDate joinDateEnd;
    private Integer maxParticipate;
    public CompListDTO(CompList entity){
        this.idx=entity.getIdx();
        this.compName=entity.getCompName();
        this.compDateStart=entity.getCompDateStart();
        this.compDateEnd=entity.getCompDateEnd();
        this.joinDateStart=entity.getJoinDateStart();
        this.joinDateEnd=entity.getJoinDateEnd();
        this.maxParticipate=entity.getMaxParticipate();
    }

    public CompList toEntity(){
        return CompList.builder()
                .CompName(compName)
                .CompDateStart(compDateStart)
                .CompDateEnd(compDateEnd)
                .JoinDateStart(joinDateStart)
                .JoinDateEnd(joinDateEnd)
                .MaxParticipate(maxParticipate)
                .build();
    }
}
