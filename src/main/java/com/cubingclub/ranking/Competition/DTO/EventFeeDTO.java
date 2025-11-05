package com.cubingclub.ranking.Competition.DTO;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Entity.EventFee;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventFeeDTO {
    private Integer idx;
    private String eventName;   // CubeEvent의 기본키
    private Integer eventFee;

    public EventFee toEntity(Competition comp, CubeEvent cubeEvent) {
        return EventFee.builder()
                .idx(idx)
                .comp(comp)
                .cubeEvent(cubeEvent)
                .eventFee(eventFee)
                .build();
    }
}
