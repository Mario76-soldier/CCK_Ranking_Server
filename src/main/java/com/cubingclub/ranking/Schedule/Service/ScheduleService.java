package com.cubingclub.ranking.Schedule.Service;

import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.Record.DTO.RecordRequestDTO;
import com.cubingclub.ranking.Record.Service.RecordService;
import com.cubingclub.ranking.Round.Repository.RoundGroupRepository;
import com.cubingclub.ranking.Schedule.DTO.ScheduleCreateDTO;
import com.cubingclub.ranking.Schedule.DTO.ScheduleDTO;
import com.cubingclub.ranking.Schedule.DTO.SchedulePartDTO;
import com.cubingclub.ranking.Schedule.DTO.ScheduleRequestDTO;
import com.cubingclub.ranking.Schedule.Entity.PlayType;
import com.cubingclub.ranking.Schedule.Entity.Schedule;
import com.cubingclub.ranking.Schedule.Repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final AccountRepository accountRepository;
    private final CompetitionRepository competitionRepository;
    private final RoundGroupRepository roundGroupRepository;
    private final ScheduleRepository scheduleRepository;
    private final RecordService recordService;

    public void createSchedule(ScheduleRequestDTO scheduleRequestDTO){
        ScheduleCreateDTO scheduleCreateDTO=new ScheduleCreateDTO();
        Competition competition=competitionRepository.findByIdx(scheduleRequestDTO.getCompIdx());
        scheduleCreateDTO.setCompetition(competition);
        List<RecordRequestDTO> recordRequestDTOList=new ArrayList<>();
        for(SchedulePartDTO schedulePartDTO:scheduleRequestDTO.getGroups()){
            scheduleCreateDTO.setAccount(accountRepository.findByCckId(schedulePartDTO.getCckId()));
            scheduleCreateDTO.setRoundGroup(roundGroupRepository.findByRound_IdxAndGroupName(schedulePartDTO.getRoundIdx(), schedulePartDTO.getGroupName()));
            scheduleCreateDTO.setPlayType(schedulePartDTO.getPlayType());
            if(schedulePartDTO.getIdx()==null){
                scheduleRepository.save(scheduleCreateDTO.toCreateEntity());
                if(scheduleCreateDTO.getPlayType().equals(PlayType.PLAYER)){
                    RecordRequestDTO recordRequestDTO=new RecordRequestDTO(0,scheduleCreateDTO.getAccount().getEmail(), schedulePartDTO.getRoundIdx(), schedulePartDTO.getGroupName());
                    recordRequestDTOList.add(recordRequestDTO);
                }
            }
            else {
                scheduleCreateDTO.setIdx(schedulePartDTO.getIdx());
                scheduleRepository.save(scheduleCreateDTO.toUpdateEntity());
            }
        }
        recordService.addParticipate(recordRequestDTOList);
    }

    public List<ScheduleDTO> getScheduleList(Integer compIdx, String cckId){
        List<Schedule> scheduleList=scheduleRepository.findByCompetition_IdxAndAccount_CckIdOrderByRoundGroup_Round_EventStartAsc(compIdx, cckId);
        List<ScheduleDTO> scheduleDTOList=new ArrayList<>();
        for(Schedule schedule: scheduleList){
            ScheduleDTO scheduleDTO=new ScheduleDTO(schedule);
            scheduleDTOList.add(scheduleDTO);
        }
        return scheduleDTOList;
    }
    public List<ScheduleDTO> getAllSchedule(Integer compIdx){
        List<Schedule> scheduleList=scheduleRepository.findByCompetiton(compIdx);
        List<ScheduleDTO> scheduleDTOList=new ArrayList<>();
        for(Schedule schedule: scheduleList){
            ScheduleDTO scheduleDTO=new ScheduleDTO(schedule);
            scheduleDTOList.add(scheduleDTO);
        }
        return scheduleDTOList;
    }
}
