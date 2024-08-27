package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.Participate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ParticipateDTO {
    private Integer idx;
    private Integer pdx;
    private String userName;
    private String eventName;
    private String email;
    private Integer round;
    private Integer m1;
    private Float s1;
    private Integer m2;
    private Float s2;
    private Integer m3;
    private Float s3;
    private Integer m4;
    private Float s4;
    private Integer m5;
    private Float s5;
    private Integer singleM;
    private Float singleS;
    private Integer avgM;
    private Float avgS;
    private Integer ranking;
    private String checker1;
    private String checker2;
    public ParticipateDTO(Participate entity){
        this.idx=entity.getIdx();
        this.pdx=entity.getPdx();
        this.userName=entity.getUserName();
        this.eventName=entity.getEventName();
        this.email=entity.getEmail();
        this.round=entity.getRound();
        this.m1=entity.getM1();
        this.s1=entity.getS1();
        this.m2=entity.getM2();
        this.s2=entity.getS2();
        this.m3=entity.getM3();
        this.s3=entity.getS3();
        this.m4=entity.getM4();
        this.s4=entity.getS4();
        this.m5=entity.getM5();
        this.s5=entity.getS5();
        this.singleM=entity.getSingleM();
        this.singleS=entity.getSingleS();
        this.avgM=entity.getAvgM();
        this.avgS=entity.getAvgS();
        this.ranking=entity.getRanking();
        this.checker1=entity.getChecker1();
        this.checker2=entity.getChecker2();
    }
    public ParticipateDTO(String userName, Integer pdx, String eventName, String email, Integer advanceRoundIdx) {
        this.userName=userName;
        this.pdx=pdx;
        this.eventName=eventName;
        this.email=email;
        this.round=advanceRoundIdx;
    }
    public Participate toEntity(){
        return Participate.builder()
                .UserName(userName)
                .Pdx(pdx)
                .EventName(eventName)
                .Email(email)
                .Round(round)
                .build();
    }
}
