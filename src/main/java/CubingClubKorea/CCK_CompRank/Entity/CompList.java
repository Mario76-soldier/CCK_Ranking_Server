package CubingClubKorea.CCK_CompRank.Entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table(name="CompList")
public class CompList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Idx")
    private int idx;

    @Column(name = "CompName")
    private String compName;

    @Column(name = "CompDate")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date compDate;

    @Column(name = "CountRound")
    private int countRound;

    @Column(name = "startDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @Column(name = "endDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @Column(name = "maxParticipate")
    private Integer maxParticipate;

    @Builder
    public CompList(String CompName, Date CompDate, int CountRound, LocalDateTime StartDate, LocalDateTime EndDate, Integer MaxParticipate){
        this.compName=CompName;
        this.compDate=CompDate;
        this.countRound=CountRound;
        this.startDate=StartDate;
        this.endDate=EndDate;
        this.maxParticipate=MaxParticipate;
    }

}
