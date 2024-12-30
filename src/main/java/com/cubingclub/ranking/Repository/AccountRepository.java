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
    @Query("SELECT a.email, a.name, a.enName, a.birthDate, a.state, a.sex, a.phoneNum, a.position FROM Account a")
    List<AccountListDTO> findAllList();
}
