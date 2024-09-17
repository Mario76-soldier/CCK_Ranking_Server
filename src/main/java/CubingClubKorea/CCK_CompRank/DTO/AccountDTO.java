package CubingClubKorea.CCK_CompRank.DTO;

import CubingClubKorea.CCK_CompRank.Entity.Account;
import CubingClubKorea.CCK_CompRank.Entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AccountDTO {
    @NotBlank
    private String email;
    private String name;
    @NotBlank
    private String password;
    private String passwordCheck;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;
    private String state;
    private String sex;
    private String phoneNum;
    private String authNum;
    public Account toEntity(String encodedPassword){
        return Account.builder()
                .email(this.email)
                .name(this.name)
                .password(encodedPassword)
                .position(UserRole.ROLE_CUSTOMER)
                .birthDate(this.birthDate)
                .state(this.state)
                .sex(this.sex)
                .phoneNum(this.phoneNum)
                .build();
    }
}
