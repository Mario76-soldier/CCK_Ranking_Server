package com.cubingclub.ranking.CubeEvent.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table
public class CubeEvent {
    @Id
    @Column(length=20)
    private String eventName;
    @Column(length=15)
    private String avgCalc;
    @Column
    private ColorType colorType;
    @Builder
    public CubeEvent(String EventName, String AvgCalc, ColorType colorType){
        this.eventName=EventName;
        this.avgCalc=AvgCalc;
        this.colorType=colorType;
    }
}
