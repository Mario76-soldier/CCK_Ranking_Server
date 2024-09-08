package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.Participate;
import CubingClubKorea.CCK_CompRank.Entity.Round;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
public class ParticipateDTO {
    private Integer idx;
    private Integer pdx;
    private String userName;
    private String email;
    private Round round;
    private BigDecimal first;
    private BigDecimal second;
    private BigDecimal third;
    private BigDecimal fourth;
    private BigDecimal fifth;
    private BigDecimal single;
    private BigDecimal average;
    private Integer ranking;
    private String checker1;
    private String checker2;
    public ParticipateDTO(Participate entity){
        this.idx=entity.getIdx();
        this.pdx=entity.getPdx();
        this.userName=entity.getUserName();
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
    public ParticipateDTO(String userName, Integer pdx, String email, Round round) {
        this.userName=userName;
        this.pdx=pdx;
        this.email=email;
        this.round=round;
    }
    public Participate toEntity(){
        return Participate.builder()
                .UserName(userName)
                .Pdx(pdx)
                .Email(email)
                .Round(round)
                .build();
    }
}
