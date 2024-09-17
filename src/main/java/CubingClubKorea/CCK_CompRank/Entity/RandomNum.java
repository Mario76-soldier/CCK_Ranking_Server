package CubingClubKorea.CCK_CompRank.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table
public class RandomNum {
    @Id
    @Column(length=7)
    private String randNum;
    @Column
    private String email;
    @Column
    private LocalDateTime expire;

    @Builder
    public RandomNum(String randNum, String email, LocalDateTime expire){
        this.randNum=randNum;
        this.email=email;
        this.expire=expire;
    }
}
