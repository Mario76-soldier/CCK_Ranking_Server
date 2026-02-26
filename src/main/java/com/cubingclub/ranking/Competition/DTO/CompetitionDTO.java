package com.cubingclub.ranking.Competition.DTO;

import com.cubingclub.ranking.Competition.Entity.Competition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionDTO {
    private Integer idx;
    private String compName;
    private LocalDate compDateStart;
    private LocalDate compDateEnd;
    private LocalDateTime joinDateStart;
    private LocalDateTime joinDateEnd;
    private Integer baseFee;
    private String location;
    private Integer maxParticipate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EventFeeDTO> eventFeeList;

    public CompetitionDTO(Competition entity){
        this.idx=entity.getIdx();
        this.compName=entity.getCompName();
        this.compDateStart=entity.getCompDateStart();
        this.compDateEnd=entity.getCompDateEnd();
        this.joinDateStart=entity.getJoinDateStart();
        this.joinDateEnd=entity.getJoinDateEnd();
        this.baseFee=entity.getBaseFee();
        this.location=entity.getLocation();
        this.maxParticipate=entity.getMaxParticipate();
        this.createdAt=entity.getCreatedAt();
        this.updatedAt=entity.getUpdatedAt();
        this.eventFeeList=entity.getEventFees().stream().map(EventFeeDTO::new).toList();
    }
}
