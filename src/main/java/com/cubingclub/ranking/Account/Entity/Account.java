package com.cubingclub.ranking.Account.Entity;

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
    @Column(length=40)
    private String enName;
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
    private Gender gender;

    public Account roleUpdate(UserRole position){
        this.position=position;
        return this;
    }
}