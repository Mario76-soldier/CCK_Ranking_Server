package CubingClubKorea.CCK_CompRank.Entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name="Round")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Idx")
    private Integer idx;

    @Column(name="CompIdx")
    private Integer compIdx;

    @Column(name="Seq")
    private Integer seq;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="EventName")
    private CubeEvent cubeEvent;

    @Column(name="Round")
    private String round;

    @Column(name="EventStart")
    private LocalDateTime eventStart;

    @Column(name="EventEnd")
    private LocalDateTime eventEnd;

    @Column(name="Advance")
    private Integer advance;

    @Builder
    public Round(Integer Idx, Integer CompIdx, Integer Seq, CubeEvent cubeEvent, String Round, LocalDateTime EventStart, LocalDateTime EventEnd, Integer Advance){
        this.idx=Idx;
        this.compIdx=CompIdx;
        this.seq=Seq;
        this.cubeEvent=cubeEvent;
        this.round=Round;
        this.eventStart=EventStart;
        this.eventEnd=EventEnd;
        this.advance=Advance;
    }
}
