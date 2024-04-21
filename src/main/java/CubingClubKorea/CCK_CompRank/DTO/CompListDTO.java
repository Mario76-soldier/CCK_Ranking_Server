package CubingClubKorea.CCK_CompRank.DTO;


import CubingClubKorea.CCK_CompRank.Entity.CompList;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CompListDTO {
    private int Idx;

    private String CompName;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date CompDate;

    private int CountRound;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private Integer maxParticipate;

    public CompListDTO(CompList entity){
        this.Idx=entity.getIdx();
        this.CompName=entity.getCompName();
        this.CompDate=entity.getCompDate();
        this.CountRound=entity.getCountRound();
        this.startDate=entity.getStartDate();
        this.endDate=entity.getEndDate();
        this.maxParticipate=entity.getMaxParticipate();
    }

    public CompList toEntity(){
        return CompList.builder()
                .CompName(CompName)
                .CompDate(CompDate)
                .CountRound(CountRound)
                .StartDate(startDate)
                .EndDate(endDate)
                .MaxParticipate(maxParticipate)
                .build();
    }
}
