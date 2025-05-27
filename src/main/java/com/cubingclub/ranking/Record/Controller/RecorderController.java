package com.cubingclub.ranking.Record.Controller;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Competition.Service.CompetitionService;
import com.cubingclub.ranking.Record.Service.RecordService;
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
    private final RecordService recordService;
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
        RecorderResponseDTO recorderResponseDTO=recordService.getForRecorder(email, compIdx);
        if(recorderResponseDTO==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recorderResponseDTO);
    }
    @PostMapping("/record")
    public ResponseEntity<?> Record(@RequestBody RecorderDTO recorderDTO){
        recordService.updateRecordOne(recorderDTO.getAttemptIdx(), recorderDTO.getRecord(), recorderDTO.getJudgeEmail());
        AttemptDTO attemptDTO=recordService.getAttempt(recorderDTO.getAttemptIdx());
        RecordDTO recordDTO=recordService.getParticipateOne(attemptDTO.getRecordIdx());
        String avgCalc=recordDTO.getRound().getCubeEvent().getAvgCalc();
        SingleAverageDTO singleAverageDTO=new SingleAverageDTO();
        if(attemptDTO.getAttemptTry().equals(4)){
            return ResponseEntity.ok(recordService.calculateExpectation(recordDTO.getIdx()));
        }
        else if (attemptDTO.getAttemptTry().equals(5) || (attemptDTO.getAttemptTry().equals(3) && avgCalc.equals("mo3"))){
            singleAverageDTO.setSingle(recordService.formatSeconds(recordDTO.getSingle()));
            singleAverageDTO.setAverage(recordService.formatSeconds(recordDTO.getAverage()));
            return ResponseEntity.ok(singleAverageDTO);
        }
        else if(attemptDTO.getAttemptTry().equals(1) && avgCalc.equals("single")){
            singleAverageDTO.setSingle(recordService.formatSeconds(recordDTO.getSingle()));
            return ResponseEntity.ok(singleAverageDTO);
        }
        return ResponseEntity.ok().build();
    }
}