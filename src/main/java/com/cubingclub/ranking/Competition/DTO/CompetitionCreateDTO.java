package com.cubingclub.ranking.Competition.DTO;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Entity.EventFee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionCreateDTO {
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
    public Competition toEntity(){
        return Competition.builder()
                .CompName(compName)
                .CompDateStart(compDateStart)
                .CompDateEnd(compDateEnd)
                .JoinDateStart(joinDateStart)
                .JoinDateEnd(joinDateEnd)
                .baseFee(baseFee)
                .location(location)
                .MaxParticipate(maxParticipate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
