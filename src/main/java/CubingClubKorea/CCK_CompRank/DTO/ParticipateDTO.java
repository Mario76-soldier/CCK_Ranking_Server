package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.Participate;
import CubingClubKorea.CCK_CompRank.Entity.Round;
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
    private Round round;
    private Double first;
    private Double second;
    private Double third;
    private Double fourth;
    private Double fifth;
    private Double single;
    private Double average;
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
        this.first=entity.getFirst();
        this.second=entity.getSecond();
        this.third=entity.getThird();
        this.fourth=entity.getFourth();
        this.fifth=entity.getFifth();
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.ranking=entity.getRanking();
        this.checker1=entity.getChecker1();
        this.checker2=entity.getChecker2();
    }
    public ParticipateDTO(String userName, Integer pdx, String eventName, String email, Round round) {
        this.userName=userName;
        this.pdx=pdx;
        this.eventName=eventName;
        this.email=email;
        this.round=round;
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
