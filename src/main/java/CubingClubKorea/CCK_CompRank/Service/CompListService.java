package CubingClubKorea.CCK_CompRank.Service;

import CubingClubKorea.CCK_CompRank.DTO.CompListDTO;
import CubingClubKorea.CCK_CompRank.DTO.RoundDTO;
import CubingClubKorea.CCK_CompRank.Repository.CompListRepository;
import CubingClubKorea.CCK_CompRank.Entity.CompList;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompListService {
    private final CompListRepository complistRepository;
    private final RoundService roundService;
    public Integer create(CompListDTO comp){
        return complistRepository.save(comp.toEntity()).getIdx();
    }
    public CompListDTO getOne(Integer idx){
        return new CompListDTO(complistRepository.findByIdx(idx));
    }
    public List<CompListDTO> getListToday(LocalDate date){
        List<CompList> list=complistRepository.findByCompDateEndGreaterThanEqualAndCompDateStartLessThanEqualOrderByCompDateStartDesc(date, date);
        List<CompListDTO> listDTO=new ArrayList<>();
        for (CompList now:list) {
            listDTO.add(new CompListDTO(now));
        }
        return listDTO;
    }
    public List<CompListDTO> getListPast(LocalDate date){
        List<CompList> list=complistRepository.findByCompDateEndBeforeOrderByCompDateStartDesc(date);
        List<CompListDTO> listDTO=new ArrayList<>();
        for (CompList past:list) {
            listDTO.add(new CompListDTO(past));
        }
        return listDTO;
    }
    public List<CompListDTO> getListFuture(LocalDate date){
        List<CompList> list=complistRepository.findByCompDateStartAfterOrderByCompDateStartDesc(date);
        List<CompListDTO> listDTO=new ArrayList<>();
        for (CompList future:list) {
            listDTO.add(new CompListDTO(future));
        }
        return listDTO;
    }
    public List<CompListDTO> getListJoin(LocalDate date){
        List<CompList> list=complistRepository.findByJoinDateEndAfterAndJoinDateStartBefore(date, date);
        List<CompListDTO> listDTO=new ArrayList<>();
        for (CompList join:list) {
            listDTO.add(new CompListDTO(join));
        }
        return listDTO;
    }
    public void deleteByIdx(int Idx){
        List<RoundDTO> roundList=roundService.getCompRound(Idx);
        for(RoundDTO round:roundList) {
            roundService.deleteByRoundIdx(round.getIdx());
        }
        complistRepository.deleteByIdx(Idx);
    }
}
