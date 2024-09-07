package CubingClubKorea.CCK_CompRank.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table
public class Participate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idx;
    @Column
    private Integer pdx;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn
    private Round round;
    @Column(length=40)
    private String userName;
    @Column(length=15)
    private String eventName;
    @Column(length=30)
    private String email;
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Double first;
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Double second;
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Double third;
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Double fourth;
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Double fifth;
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Double single;
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Double average;
    @Column
    private Integer ranking;
    @Column(length=20)
    private String checker1;
    @Column(length=20)
    private String checker2;

    @Builder
    public Participate(String UserName, Integer Pdx, String Email, String EventName, Round Round){
        this.userName=UserName;
        this.pdx=Pdx;
        this.eventName=EventName;
        this.email=Email;
        this.round=Round;
    }

    public Participate update(Double First, Double Second, Double Third, Double Fourth, Double Fifth, Double Single, Double Average, Integer Ranking, String Checker1){
        this.first=First;
        this.second=Second;
        this.third=Third;
        this.fourth=Fourth;
        this.fifth=Fifth;
        this.single=Single;
        this.average=Average;
        this.ranking=Ranking;
        this.checker1=Checker1;
        return this;
    }

    public Participate update(String Checker2){
        this.checker2=Checker2;
        return this;
    }

}
