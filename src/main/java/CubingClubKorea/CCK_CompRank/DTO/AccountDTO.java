package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.Account;
import CubingClubKorea.CCK_CompRank.Entity.UserRole;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AccountDTO {
    private String email;
    private String name;
    private String password;
    private String passwordCheck;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthDate;
    private String state;
    private String sex;
    private String phoneNum;


    public Account toEntity(String encodedPassword){
        return Account.builder()
                .email(this.email)
                .name(this.name)
                .password(encodedPassword)
                .position(UserRole.customer)
                .birthDate(this.birthDate)
                .state(this.state)
                .sex(this.sex)
                .phoneNum(this.phoneNum)
                .build();
    }

}
