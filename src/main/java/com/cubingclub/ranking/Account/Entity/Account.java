package com.cubingclub.ranking.Account.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column
    private String email;
    @Column
    private String name;
    @Column
    private String enName;
    @Column
    @JsonIgnore
    private String password;
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole position;
    @Column
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;
    @Column(length=15)
    private String state;
    @Column
    private Gender gender;
    @Column(unique = true)
    private String cckId;

    public Account roleUpdate(UserRole position){
        this.position=position;
        return this;
    }
}