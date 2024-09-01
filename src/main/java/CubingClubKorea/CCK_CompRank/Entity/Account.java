package CubingClubKorea.CCK_CompRank.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Accounts")
public class Account {
    @Id
    @Column(name="email", length=30)
    private String email;

    @Column(name="name", length=30)
    private String name;

    @Column(name="password")
    private String password;

    @Column(name="position")
    @Enumerated(EnumType.STRING)
    private UserRole position;

    @Column(name="birthDate")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;

    @Column(name="state", length=15)
    private String state;

    @Column(name="sex", length=10)
    private String sex;

    @Column(name="phoneNum", length=20)
    private String phoneNum;
}
