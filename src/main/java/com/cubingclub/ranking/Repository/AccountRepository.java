package com.cubingclub.ranking.Repository;

import com.cubingclub.ranking.DTO.AccountListDTO;
import com.cubingclub.ranking.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Boolean existsByEmail(String email);
    Optional<Account> findByEmail(String email);
    @Query("SELECT a.email as email, a.name as name, a.enName as enName, a.birthDate as birthDate, a.state as state, a.sex as sex, a.phoneNum as phoneNum, a.position as position FROM Account a")
    List<AccountListDTO> findAllList();
}
