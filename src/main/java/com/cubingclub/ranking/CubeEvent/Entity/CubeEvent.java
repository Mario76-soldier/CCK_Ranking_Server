package com.cubingclub.ranking.CubeEvent.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CubeEvent {
    @Id
    @Column(length=20)
    private String eventName;
    @Column(length=15, nullable = false)
    private String avgCalc;
    private ColorType colorType;
}
