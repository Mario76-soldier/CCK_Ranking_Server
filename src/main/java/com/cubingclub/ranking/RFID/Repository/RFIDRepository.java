package com.cubingclub.ranking.RFID.Repository;

import com.cubingclub.ranking.Account.DTO.AccountListDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.RFID.Entity.RFID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RFIDRepository extends JpaRepository<RFID, Integer> {
    @Query("select a.email from RFID r join Account a where r.uid=:uid")
    String findEmailByUid(@Param(value = "uid")String uid);
    void deleteByAccount_email(String email);
}
