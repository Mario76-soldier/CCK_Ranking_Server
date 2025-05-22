package com.cubingclub.ranking.Round.Service;

import com.cubingclub.ranking.Record.Repository.RecordRepository;
import com.cubingclub.ranking.Round.DTO.CreateRoundDTO;
import com.cubingclub.ranking.Round.DTO.RoundDTO;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Round.DTO.RoundGroupDTO;
import com.cubingclub.ranking.Round.Repository.RoundGroupRepository;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Scrambler.Repository.ScramblerRepository;
import com.cubingclub.ranking.Scrambler.Service.ScramblerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoundService {
    private final RecordRepository recordRepository;
    private final RoundRepository roundRepository;
    private final RoundGroupRepository roundGroupRepository;
    private final ScramblerRepository scramblerRepository;
    private final CompetitionRepository competitionRepository;
    private final CubeEventRepository cubeEventRepository;
    private final ScramblerService scramblerService;
    public List<Integer> create(List<CreateRoundDTO> roundList){
        List<Integer> list=new ArrayList<>();
        Integer roundIdx;
        for(CreateRoundDTO roundDTO:roundList){
            if(roundDTO.getIdx()!=null) {
                roundIdx=roundRepository.save(roundDTO.toEntity(competitionRepository, cubeEventRepository)).getIdx();
                List<String> roundGroup=roundDTO.getGroupList();
                list.add(roundIdx);
                for (String s : roundGroup) {
                    if(roundGroupRepository.findByRound_IdxAndGroupName(roundIdx, s)==null) {
                        RoundGroupDTO roundGroupDTO = new RoundGroupDTO(roundIdx, s);
                        roundGroupRepository.save(roundGroupDTO.toEntity(roundRepository));
                    }
                }
                scramblerService.saveScramble(roundIdx, roundGroup);
            }
            else{
                if(competitionRepository.findByIdx(roundDTO.getComp())!=null) {
                    roundIdx=roundRepository.save(roundDTO.toEntity(competitionRepository, cubeEventRepository)).getIdx();
                    List<String> roundGroup=roundDTO.getGroupList();
                    list.add(roundIdx);
                    for (String s : roundGroup) {
                        RoundGroupDTO roundGroupDTO = new RoundGroupDTO(roundIdx, s);
                        roundGroupRepository.save(roundGroupDTO.toEntity(roundRepository));
                    }
                    scramblerService.saveScramble(roundIdx, roundGroup);
                }
            }
        }
        return list;
    }
    public RoundDTO getOne(Integer idx){
        Round round=roundRepository.findByIdx(idx);
        if(round==null){
            return null;
        }
        return new RoundDTO(round);
    }
    public Map<String, List<RoundDTO>> getRoundList(Integer compIdx, LocalDateTime date){
        List<Round> list=roundRepository.findRoundList(compIdx);

        Map<String, List<RoundDTO>> result = new HashMap<>();
        result.put("past", new ArrayList<>());
        result.put("now", new ArrayList<>());
        result.put("future", new ArrayList<>());

        for(Round r : list){
            RoundDTO roundDTO=new RoundDTO(r);
            if(r.getEventEnd().isBefore(date)){
                result.get("past").add(roundDTO);
            }
            else if ((r.getEventEnd().isEqual(date) || r.getEventEnd().isAfter(date)) && (r.getEventStart().isEqual(date)||r.getEventStart().isBefore(date))){
                result.get("now").add(roundDTO);
            }
            else{
                result.get("future").add(roundDTO);
            }
        }
        return result;
    }
    public void deleteByRoundIdx(Integer roundIdx){
        recordRepository.deleteByRound_Idx(roundIdx);
        scramblerRepository.deleteByRound_Idx(roundIdx);
        roundRepository.deleteByIdx(roundIdx);
    }
    public void updateAdvance(Integer roundIdx, Integer advance){
        Round round=roundRepository.findByIdx(roundIdx);
        RoundDTO roundDTO=new RoundDTO(round);
        roundDTO.setAdvance(advance);
        roundRepository.save(roundDTO.toEntity(round.getRoundGroup()));
    }

    public List<String> getGroupList(Integer roundIdx){
        return roundGroupRepository.findGroupByRound_Idx(roundIdx);
    }
}
