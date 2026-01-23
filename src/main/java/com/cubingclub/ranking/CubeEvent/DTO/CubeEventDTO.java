package com.cubingclub.ranking.CubeEvent.DTO;


import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CubeEventDTO {
    private String eventName;
    private String avgCalc;
    private ColorType colorType;
    public CubeEventDTO(CubeEvent entity){
        this.eventName=entity.getEventName();
        this.avgCalc=entity.getAvgCalc();
        this.colorType=entity.getColorType();
    }
}
