package com.cubingclub.ranking.Competition.Service;

import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Competition.DTO.CompetitionCreateDTO;
import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Competition.DTO.CompetitorDTO;
import com.cubingclub.ranking.Competition.DTO.EventFeeDTO;
import com.cubingclub.ranking.Competition.Entity.Competitor;
import com.cubingclub.ranking.Competition.Entity.EventFee;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Repository.CompetitorRepository;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.RFID.Repository.RFIDRepository;
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
    private final CompetitorRepository competitorRepository;
    private final CubeEventRepository cubeEventRepository;
    private final RFIDRepository rfidRepository;
    public Integer create(CompetitionCreateDTO compDTO) {
        Competition competition;

        if (compDTO.getIdx() != null) {
            competition = competitionRepository.findByIdx(compDTO.getIdx());
            competition.update(compDTO);
            competition.getEventFees().clear();
        } else {
            compDTO.setCreatedAt(LocalDateTime.now());
            compDTO.setUpdatedAt(LocalDateTime.now());
            competition = compDTO.toEntity();
        }

        if (compDTO.getEventFeeList() != null) {
            for (EventFeeDTO feeDTO : compDTO.getEventFeeList()) {
                CubeEvent cubeEvent = cubeEventRepository.findByEventName(feeDTO.getEventName());
                EventFee feeEntity = feeDTO.toEntity(competition, cubeEvent);
                competition.getEventFees().add(feeEntity);
            }
        }

        return competitionRepository.save(competition).getIdx();
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
    public void deleteByIdx(Integer idx){
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
