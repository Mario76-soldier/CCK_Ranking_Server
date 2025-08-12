package com.cubingclub.ranking.RFID.Entity;

import com.cubingclub.ranking.Account.Entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class RFID {
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_email")
    private Account account;
    @Id
    @Column
    private String uid;
}