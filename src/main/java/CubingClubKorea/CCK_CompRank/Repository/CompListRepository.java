package CubingClubKorea.CCK_CompRank.Repository;

import CubingClubKorea.CCK_CompRank.Entity.CompList;
import CubingClubKorea.CCK_CompRank.Entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CompListRepository extends JpaRepository<CompList, Integer> {
    CompList findByIdx(Integer idx);
    List<CompList> findAll();
    List<CompList> findByCompDateBefore(Date date);
    List<CompList> findByCompDate(Date date);
    List<CompList> findByCompDateAfter(Date date);

    List<CompList> findByStartDateBeforeAndEndDateAfter(LocalDateTime date, LocalDateTime date2);

    void deleteByIdx(int idx);
}
