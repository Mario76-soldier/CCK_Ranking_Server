package CubingClubKorea.CCK_CompRank.Repository;

import CubingClubKorea.CCK_CompRank.DTO.AccountListDTO;
import CubingClubKorea.CCK_CompRank.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Boolean existsByEmail(String email);
    Optional<Account> findByEmail(String email);
    @Query("SELECT a.email, a.name, a.birthDate, a.state, a.sex, a.phoneNum, a.position FROM Account a")
    List<AccountListDTO> findAllList();
}
