package com.cubingclub.ranking.Participate.Controller;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.DTO.AccountResponseDTO;
import com.cubingclub.ranking.Account.Service.AccountService;
import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Participate.DTO.ParticipateDTO;
import com.cubingclub.ranking.Participate.DTO.RecorderDTO;
import com.cubingclub.ranking.Participate.DTO.RecorderResponseDTO;
import com.cubingclub.ranking.Competition.Service.CompetitionService;
import com.cubingclub.ranking.Participate.DTO.SingleAverageDTO;
import com.cubingclub.ranking.Participate.Service.ParticipateService;
import com.cubingclub.ranking.RFID.Entity.RFID;
import com.cubingclub.ranking.RFID.Service.RFIDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

//CCK Recorder를 사용하기 위한 Controller
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recorder")
public class RecorderController {
    private final CompetitionService competitionService;
    private final ParticipateService participateService;
    private final RFIDService rfidService;
    @GetMapping("/comp")
    public ResponseEntity<List<CompetitionDTO>> CompIdx(){
        return ResponseEntity.ok(competitionService.getCompListToday(LocalDate.now()));
    }
    @GetMapping("/info/{uid}")
    public ResponseEntity<AccountDTO> GetAccountInfo(@PathVariable(name="uid")String uid){
        AccountDTO accountDTO=rfidService.getAccountInfo(uid);
        if(accountDTO==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(accountDTO);
    }
    @GetMapping("/record/{email}/{compIdx}")
    public ResponseEntity<RecorderResponseDTO> CurrentRecord(@PathVariable String email, @PathVariable Integer compIdx){
        RecorderResponseDTO recorderResponseDTO=participateService.getForRecorder(email, compIdx);
        if(recorderResponseDTO==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recorderResponseDTO);
    }
    @PostMapping("/record")
    public ResponseEntity<?> Record(@RequestBody RecorderDTO recordDTO){
        participateService.updateRecordOne(recordDTO.getIdx(), recordDTO.getSeq(), recordDTO.getRecord(), recordDTO.getJudgeEmail());
        ParticipateDTO participateDTO=participateService.getParticipateOne(recordDTO.getIdx());
        String eventName=participateDTO.getRound().getCubeEvent().getEventName();
        SingleAverageDTO singleAverageDTO=new SingleAverageDTO();
        if(recordDTO.getSeq().equals(4)){
            return ResponseEntity.ok(participateService.calculateExpectation(recordDTO.getIdx()));
        }
        else if (recordDTO.getSeq().equals(5) || (recordDTO.getSeq().equals(3) && eventName.equals("bo3"))){
            singleAverageDTO.setSingle(participateService.formatSeconds(participateDTO.getSingle()));
            singleAverageDTO.setAverage(participateService.formatSeconds(participateDTO.getAverage()));
            return ResponseEntity.ok(singleAverageDTO);
        }
        else if(recordDTO.getSeq().equals(1) && eventName.equals("single")){
            singleAverageDTO.setSingle(participateService.formatSeconds(participateDTO.getSingle()));
            return ResponseEntity.ok(singleAverageDTO);
        }
        return ResponseEntity.ok().build();
    }
}