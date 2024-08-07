package CubingClubKorea.CCK_CompRank.Repository;

import CubingClubKorea.CCK_CompRank.Entity.CompList;
import CubingClubKorea.CCK_CompRank.Entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CompListRepository extends JpaRepository<CompList, Integer> {
    CompList findByIdx(Integer idx);
    List<CompList> findAll();
    List<CompList> findByCompDateEndBeforeOrderByCompDateStartDesc(LocalDate date);
    List<CompList> findByCompDateEndAfterAndCompDateStartBeforeOrderByCompDateStartDesc(LocalDate date1, LocalDate date2);
    List<CompList> findByCompDateStartAfterOrderByCompDateStartDesc(LocalDate date);

    List<CompList> findByJoinDateEndAfterAndJoinDateStartBefore(LocalDate date1, LocalDate date2);
    void deleteByIdx(int idx);
}
