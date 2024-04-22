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
@Table(name="Participate")
public class Participate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Idx")
    private Integer idx;

    @Column(name="UserName", length=40)
    private String userName;

    @Column(name="EventName", length=15)
    private String eventName;

    @Column(name="Email", length=30)
    private String email;

    @Column(name="Round")
    private Integer round;

    @Column(name="m1")
    private Integer m1;

    @Column(name = "s1")
    private Float s1;

    @Column(name="m2")
    private Integer m2;

    @Column(name = "s2")
    private Float s2;

    @Column(name="m3")
    private Integer m3;

    @Column(name = "s3")
    private Float s3;

    @Column(name="m4")
    private Integer m4;

    @Column(name = "s4")
    private Float s4;

    @Column(name="m5")
    private Integer m5;

    @Column(name = "s5")
    private Float s5;

    @Column(name="SingleM")
    private Integer singleM;

    @Column(name = "SingleS")
    private Float singleS;

    @Column(name="AvgM")
    private Integer avgM;

    @Column(name = "AvgS")
    private Float avgS;

    @Column(name="ranking")
    private Integer ranking;

    @Column(name="Checker1", length=20)
    private String checker1;

    @Column(name = "Checker2", length=20)
    private String checker2;

    @Builder
    public Participate(String UserName, String Email, String EventName, Integer Round){
        this.userName=UserName;
        this.eventName=EventName;
        this.email=Email;
        this.round=Round;
    }

    public Participate update( Integer m1, Float s1, Integer m2, Float s2,
                               Integer m3, Float s3, Integer m4, Float s4,
                               Integer m5, Float s5,Integer SingleM, Float SingleS,
                               Integer AvgM, Float AvgS, Integer Ranking, String Checker1){
        this.m1=m1;
        this.s1=s1;
        this.m2=m2;
        this.s2=s2;
        this.m3=m3;
        this.s3=s3;
        this.m4=m4;
        this.s4=s4;
        this.m5=m5;
        this.s5=s5;
        this.singleM=SingleM;
        this.singleS=SingleS;
        this.avgM=AvgM;
        this.avgS=AvgS;
        this.ranking=Ranking;
        this.checker1=Checker1;
        return this;
    }

    public Participate update(String Checker2){
        this.checker2=Checker2;
        return this;
    }

}
