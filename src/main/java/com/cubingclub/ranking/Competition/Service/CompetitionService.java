package com.cubingclub.ranking.Competition.Service;

import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Competition.DTO.CompetitorDTO;
import com.cubingclub.ranking.Competition.Entity.Competitor;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Repository.CompetitorRepository;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import com.cubingclub.ranking.RFID.Repository.RFIDRepository;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompetitionService {
    private final AccountRepository accountRepository;
    private final CompetitionRepository competitionRepository;
    private final RoundRepository roundRepository;
    private final RecordRepository recordRepository;
    private final CompetitorRepository competitorRepository;
    private final RFIDRepository rfidRepository;
    public Integer create(CompetitionDTO comp){
        if(comp.getIdx()!=null){
            return competitionRepository.save(comp.toEntity()).getIdx();
        }
        return competitionRepository.save(comp.toEntityNoIdx()).getIdx();
    }
    public CompetitionDTO getOne(Integer idx){
        return new CompetitionDTO(competitionRepository.findByIdx(idx));
    }
    public Map<String, List<CompetitionDTO>> getCompList(LocalDate date){
        List<Competition> list=competitionRepository.findAllByOrderByCompDateStartDesc();

        Map<String, List<CompetitionDTO>> result = new HashMap<>();
        result.put("past", new ArrayList<>());
        result.put("now", new ArrayList<>());
        result.put("future", new ArrayList<>());

        for(Competition c : list){
            CompetitionDTO competitionDTO=new CompetitionDTO(c);
            if(c.getCompDateEnd().isBefore(date)){
                result.get("past").add(competitionDTO);
            }
            else if ((c.getCompDateEnd().isEqual(date) || c.getCompDateEnd().isAfter(date)) && (c.getCompDateStart().isEqual(date)||c.getCompDateStart().isBefore(date))){
                result.get("now").add(competitionDTO);
            }
            else{
                result.get("future").add(competitionDTO);
            }
        }
        return result;
    }
    public List<CompetitionDTO> getCompListToday(LocalDate date){
        List<Competition> list=competitionRepository.findByCompDateStartLessThanEqualAndCompDateEndGreaterThanEqual(date, date);
        List<CompetitionDTO> listDTO=new ArrayList<>();
        for(Competition c : list){
            listDTO.add(new CompetitionDTO(c));

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
    public void deleteByIdx(Integer idx){
        recordRepository.deleteByRound_Comp_Idx(idx);
        roundRepository.deleteByComp_Idx(idx);
        competitionRepository.deleteByIdx(idx);
    }
    public List<CompetitorDTO> getCompetitorList(Integer compIdx){
        List<Competitor> list=competitorRepository.findByCompetition_Idx(compIdx);
        List<CompetitorDTO> competitorDTOList=new ArrayList<>();
        for(Competitor competitor:list){
            competitorDTOList.add(new CompetitorDTO(competitor));
        }
        return competitorDTOList;
    }
    public int updateAttend(Integer compIdx, String uid){
        Competitor competitor=competitorRepository.findByCompetition_IdxAndAccount_email(compIdx, rfidRepository.findEmailByUid(uid));
        if(competitor==null){
            return -1;
        }
        CompetitorDTO competitorDTO=new CompetitorDTO(competitor);
        competitorDTO.setAttended(true);
        competitorRepository.save(competitorDTO.toEntity(accountRepository));
        return 0;
    }
}
