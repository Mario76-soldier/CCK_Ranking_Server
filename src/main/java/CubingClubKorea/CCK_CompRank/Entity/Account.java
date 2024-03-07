package CubingClubKorea.CCK_CompRank.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Accounts")
public class Account {
    @Id
    @Column(name="email")
    private String email;

    @Column(name="name")
    private String name;

    @Column(name="password")
    private String password;

    @Column(name="position")
    @Enumerated(EnumType.STRING)
    private UserRole position;

    @Column(name="birthDate")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthDate;

    @Column(name="state")
    private String state;

    @Column(name="sex")
    private String sex;

    @Column(name="phoneNum")
    private String phoneNum;
}
