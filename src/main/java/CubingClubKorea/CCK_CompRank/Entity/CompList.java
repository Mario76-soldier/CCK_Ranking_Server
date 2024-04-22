package CubingClubKorea.CCK_CompRank.Entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name="CompList")
public class CompList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Idx")
    private Integer idx;

    @Column(name = "CompName")
    private String compName;

    @Column(name = "CompDateStart")
    private LocalDateTime compDateStart;

    @Column(name = "CompDateEnd")
    private LocalDateTime compDateEnd;

    @Column(name = "joinDateStart")
    private LocalDateTime joinDateStart;

    @Column(name = "JoinDateEnd")
    private LocalDateTime joinDateEnd;

    @Column(name = "maxParticipate")
    private Integer maxParticipate;

    @Builder
    public CompList(String CompName, LocalDateTime CompDateStart, LocalDateTime CompDateEnd,LocalDateTime JoinDateStart, LocalDateTime JoinDateEnd, Integer MaxParticipate){
        this.compName=CompName;
        this.compDateStart=CompDateStart;
        this.compDateEnd=CompDateEnd;
        this.joinDateStart=JoinDateStart;
        this.joinDateEnd=JoinDateEnd;
        this.maxParticipate=MaxParticipate;
    }

}
