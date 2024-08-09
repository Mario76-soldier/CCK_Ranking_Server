package CubingClubKorea.CCK_CompRank.Repository;

import CubingClubKorea.CCK_CompRank.Entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Integer> {
    Round findByIdx(Integer idx);
    @Query("select r from Round r join fetch r.cubeEvent where r.compIdx=:compIdx and r.eventEnd<:date order by r.eventStart asc")
    List<Round> findPast(@Param(value="date")LocalDateTime date, @Param(value="compIdx")Integer compIdx);
    @Query("select r from Round r join fetch r.cubeEvent where r.compIdx=:compIdx and r.eventEnd>:date and r.eventStart<:date order by r.eventStart asc")
    List<Round> findNow(@Param(value="date")LocalDateTime date, @Param(value="compIdx")Integer compIdx);
    @Query("select r from Round r join fetch r.cubeEvent where r.compIdx=:compIdx and r.eventStart>:date order by r.eventStart asc")
    List<Round> findFuture(@Param(value="date")LocalDateTime date, @Param(value="compIdx")Integer compIdx);

    List<Round> findByCompIdx(Integer compIdx);

    @Modifying
    @Query("update Round u set u.advance=:advance where u.idx=:idx")
    void updateAdvance(@Param(value="idx")Integer idx, @Param(value = "advance")Integer advance);

    void deleteByCompIdx(Integer compIdx);
    void deleteByIdx(Integer Idx);

}
