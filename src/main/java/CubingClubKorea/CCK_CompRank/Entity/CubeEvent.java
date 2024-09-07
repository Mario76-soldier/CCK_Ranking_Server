package CubingClubKorea.CCK_CompRank.Entity;

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
    @Builder
    public CubeEvent(String EventName, String AvgCalc){
        this.eventName=EventName;
        this.avgCalc=AvgCalc;
    }
}
