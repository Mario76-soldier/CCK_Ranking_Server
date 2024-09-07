package CubingClubKorea.CCK_CompRank.Service;

import CubingClubKorea.CCK_CompRank.DTO.CompetitionDTO;
import CubingClubKorea.CCK_CompRank.DTO.RoundDTO;
import CubingClubKorea.CCK_CompRank.Repository.CompetitionRepository;
import CubingClubKorea.CCK_CompRank.Entity.Competition;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompListService {
    private final CompetitionRepository competitionRepository;
    private final RoundService roundService;
    public Integer create(CompetitionDTO comp){
        return competitionRepository.save(comp.toEntity()).getIdx();
    }
    public CompetitionDTO getOne(Integer idx){
        return new CompetitionDTO(competitionRepository.findByIdx(idx));
    }
    public List<CompetitionDTO> getListToday(LocalDate date){
        List<Competition> list=competitionRepository.findByCompDateEndGreaterThanEqualAndCompDateStartLessThanEqualOrderByCompDateStartDesc(date, date);
        List<CompetitionDTO> listDTO=new ArrayList<>();
        for (Competition now:list) {
            listDTO.add(new CompetitionDTO(now));
        }
        return listDTO;
    }
    public List<CompetitionDTO> getListPast(LocalDate date){
        List<Competition> list=competitionRepository.findByCompDateEndBeforeOrderByCompDateStartDesc(date);
        List<CompetitionDTO> listDTO=new ArrayList<>();
        for (Competition past:list) {
            listDTO.add(new CompetitionDTO(past));
        }
        return listDTO;
    }
    public List<CompetitionDTO> getListFuture(LocalDate date){
        List<Competition> list=competitionRepository.findByCompDateStartAfterOrderByCompDateStartDesc(date);
        List<CompetitionDTO> listDTO=new ArrayList<>();
        for (Competition future:list) {
            listDTO.add(new CompetitionDTO(future));
        }
        return listDTO;
    }
    public List<CompetitionDTO> getListJoin(LocalDateTime date){
        List<Competition> list=competitionRepository.findByJoinDateEndAfterAndJoinDateStartBefore(date, date);
        List<CompetitionDTO> listDTO=new ArrayList<>();
        for (Competition join:list) {
            listDTO.add(new CompetitionDTO(join));
        }
        return listDTO;
    }
    public void deleteByIdx(int Idx){
        List<RoundDTO> roundList=roundService.getCompRound(Idx);
        for(RoundDTO round:roundList) {
            roundService.deleteByRoundIdx(round.getIdx());
        }
        competitionRepository.deleteByIdx(Idx);
    }
}
