package com.cubingclub.ranking.Account.Repository;

import com.cubingclub.ranking.Account.DTO.AccountListDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Boolean existsByEmail(String email);
    Optional<Account> findByEmail(String email);
    @Query("SELECT a.email as email, a.name as name, a.enName as enName, a.birthDate as birthDate, a.state as state, a.gender as gender, a.position as position FROM Account a")
    List<AccountListDTO> findAllList();
    @Query("SELECT a.email as email, a.name as name, a.enName as enName, a.birthDate as birthDate, a.state as state, a.gender as gender, a.position as position FROM Account a " +
            "where a.name like %:searchTerm% or a.enName like %:searchTerm% or a.email like %:searchTerm%")
    List<AccountListDTO> findByName(@Param(value="searchTerm")String searchTerm);
    @Modifying
    @Query("UPDATE Account a SET a.state=:state WHERE a.email=:email")
    void updateAccountInfo(@Param(value = "state")String state, @Param(value = "email")String email);
    @Modifying
    @Query("UPDATE Account a SET a.password=:password WHERE a.email=:email")
    void updateAccountPassword(@Param(value="password")String password, @Param(value = "email")String email);
}
