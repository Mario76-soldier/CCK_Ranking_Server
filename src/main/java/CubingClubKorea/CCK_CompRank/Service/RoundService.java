package CubingClubKorea.CCK_CompRank.Service;


import CubingClubKorea.CCK_CompRank.DTO.CompListDTO;
import CubingClubKorea.CCK_CompRank.DTO.RoundDTO;
import CubingClubKorea.CCK_CompRank.Entity.CompList;
import CubingClubKorea.CCK_CompRank.Repository.RoundRepository;
import CubingClubKorea.CCK_CompRank.Entity.Round;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoundService {

    @Autowired
    private final RoundRepository roundRepository;

    public Integer create(RoundDTO round){
        return roundRepository.save(round.toEntity()).getIdx();
    }

    public RoundDTO getOne(Integer idx){
        return new RoundDTO(roundRepository.findByIdx(idx));
    }

    public List<RoundDTO> getCompRound(Integer idx){
        List<Round> list=roundRepository.findByCompIdx(idx);
        List<RoundDTO> listDTO=new ArrayList<>();
        for (Round round:list) {
            listDTO.add(new RoundDTO(round));
        }
        return listDTO;
    }
    public List<RoundDTO> getRoundNow(LocalDateTime date, Integer idx){
        List<Round> list=roundRepository.findNow(date, idx);
        List<RoundDTO> listDTO=new ArrayList<>();
        for (Round now:list) {
            listDTO.add(new RoundDTO(now));
        }
        return listDTO;
    }

    public List<RoundDTO> getRoundFuture(LocalDateTime date, Integer idx){
        List<Round> list=roundRepository.findFuture(date, idx);
        List<RoundDTO> listDTO=new ArrayList<>();
        for (Round future:list) {
            listDTO.add(new RoundDTO(future));
        }
        return listDTO;
    }
    public List<RoundDTO> getRoundPast(LocalDateTime date, Integer idx){
        List<Round> list=roundRepository.findPast(date, idx);
        List<RoundDTO> listDTO=new ArrayList<>();
        for (Round past:list) {
            listDTO.add(new RoundDTO(past));
        }
        return listDTO;
    }

    public void deleteRound(Integer idx){
        roundRepository.deleteByCompIdx(idx);
    }

    public void deleteByRoundIdx(Integer idx){
        roundRepository.deleteByIdx(idx);
    }
    public void updateAdvance(Integer idx, Integer advance){
        roundRepository.updateAdvance(idx, advance);
    }
}
