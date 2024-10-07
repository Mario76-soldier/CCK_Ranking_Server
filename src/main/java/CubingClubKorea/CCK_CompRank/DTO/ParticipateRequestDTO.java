package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.Round;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParticipateRequestDTO {
    private String userName;
    private Integer pdx;
    private String email;
    private Integer round;
}
