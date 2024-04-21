package CubingClubKorea.CCK_CompRank.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddPartDTO {
    private String UserName;

    private String Email;

    private List<Integer> Round;
}
