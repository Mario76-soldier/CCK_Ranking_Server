package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.Competition;
import CubingClubKorea.CCK_CompRank.Entity.CubeEvent;
import CubingClubKorea.CCK_CompRank.Entity.Round;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateRoundDTO {
    private Integer idx;
    private Integer comp;
    private String cubeEvent;
    private String roundName;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private Integer advance;
}
