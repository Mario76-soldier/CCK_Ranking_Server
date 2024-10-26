package com.cubingclub.ranking.DTO;


import com.cubingclub.ranking.Entity.CubeEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CubeEventDTO {
    private String EventName;
    private String AvgCalc;
    public CubeEventDTO(CubeEvent entity){
        this.EventName=entity.getEventName();
        this.AvgCalc=entity.getAvgCalc();
    }
}
