package CubingClubKorea.CCK_CompRank.Entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @ManyToOne(fetch = FetchType.EAGER)
    private Competition comp;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="eventName")
    private CubeEvent cubeEvent;
    @Column(length=30)
    private String roundName;
    @Column
    private LocalDateTime eventStart;
    @Column
    private LocalDateTime eventEnd;
    @Column
    private Integer advance;

    @Builder
    public Round(Integer Idx, Competition Comp, CubeEvent cubeEvent, String RoundName, LocalDateTime EventStart, LocalDateTime EventEnd, Integer Advance){
        this.idx=Idx;
        this.comp=Comp;
        this.cubeEvent=cubeEvent;
        this.roundName=RoundName;
        this.eventStart=EventStart;
        this.eventEnd=EventEnd;
        this.advance=Advance;
    }
}
