package CubingClubKorea.CCK_CompRank.Entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name="CompList")
public class CompList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Idx")
    private Integer idx;

    @Column(name = "CompName", length=50)
    private String compName;

    @Column(name = "CompDateStart")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate compDateStart;

    @Column(name = "CompDateEnd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate compDateEnd;

    @Column(name = "joinDateStart")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate joinDateStart;

    @Column(name = "JoinDateEnd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate joinDateEnd;

    @Column(name = "maxParticipate")
    private Integer maxParticipate;

    @Builder
    public CompList(String CompName, LocalDate CompDateStart, LocalDate CompDateEnd,LocalDate JoinDateStart, LocalDate JoinDateEnd, Integer MaxParticipate){
        this.compName=CompName;
        this.compDateStart=CompDateStart;
        this.compDateEnd=CompDateEnd;
        this.joinDateStart=JoinDateStart;
        this.joinDateEnd=JoinDateEnd;
        this.maxParticipate=MaxParticipate;
    }

}
