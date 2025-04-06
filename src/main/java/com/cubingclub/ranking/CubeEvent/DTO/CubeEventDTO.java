package com.cubingclub.ranking.CubeEvent.DTO;


import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
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
