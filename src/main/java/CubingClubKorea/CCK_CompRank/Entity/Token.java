package CubingClubKorea.CCK_CompRank.Entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String invalidRefreshToken;
    public Token(String invalidRefreshToken){
        this.invalidRefreshToken=invalidRefreshToken;
    }
}
