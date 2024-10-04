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
@Table
public class Account {
    @Id
    @Column(length=30)
    private String email;
    @Column(length=30)
    private String name;
    @Column
    private String password;
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole position;
    @Column
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;
    @Column(length=15)
    private String state;
    @Column(length=10)
    private String sex;
    @Column(length=20)
    private String phoneNum;

    public Account roleUpdate(UserRole position){
        this.position=position;
        return this;
    }
}