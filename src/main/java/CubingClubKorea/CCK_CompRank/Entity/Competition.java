package CubingClubKorea.CCK_CompRank.Entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @Column(length=50)
    private String compName;
    @Column
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate compDateStart;
    @Column
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate compDateEnd;
    @Column
    private LocalDateTime joinDateStart;
    @Column
    private LocalDateTime joinDateEnd;
    @Column
    private Integer maxParticipate;
    @Builder
    public Competition(Integer Idx, String CompName, LocalDate CompDateStart, LocalDate CompDateEnd, LocalDateTime JoinDateStart, LocalDateTime JoinDateEnd, Integer MaxParticipate){
        this.idx=Idx;
        this.compName=CompName;
        this.compDateStart=CompDateStart;
        this.compDateEnd=CompDateEnd;
        this.joinDateStart=JoinDateStart;
        this.joinDateEnd=JoinDateEnd;
        this.maxParticipate=MaxParticipate;
    }
}
