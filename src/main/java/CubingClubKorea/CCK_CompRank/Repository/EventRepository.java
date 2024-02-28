package CubingClubKorea.CCK_CompRank.Repository;

import CubingClubKorea.CCK_CompRank.DTO.CubeEventDTO;
import CubingClubKorea.CCK_CompRank.Entity.CubeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<CubeEvent, Integer> {
    CubeEvent findByEventName(String eventName);
}
