package com.cubingclub.ranking.Record.Controller;

import com.cubingclub.ranking.Auth.DTO.JudgeInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Competition.Service.CompetitionService;
import com.cubingclub.ranking.Record.Service.RecordService;
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
    private final AuthService authService;
    @GetMapping("/comp")
    public ResponseEntity<List<CompetitionDTO>> CompIdx(){
        return ResponseEntity.ok(competitionService.getCompListToday(LocalDate.now()));
    }
    @GetMapping("/info/{uid}")
    public ResponseEntity<JudgeInfo> GetAccountInfo(@PathVariable(name="uid")String uid, @RequestHeader("Authorization")String authHeader){
        String token = authHeader.substring(7);
        try {
            JudgeInfo judgeInfo = authService.getRFIDInfo(token, uid).orElse(null);
            if (judgeInfo == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(judgeInfo);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/record/{cckId}/{compIdx}")
    public ResponseEntity<RecorderResponseDTO> CurrentRecord(@PathVariable String cckId, @PathVariable Integer compIdx){
        RecorderResponseDTO recorderResponseDTO=recordService.getForRecorder(cckId, compIdx);
        if(recorderResponseDTO==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recorderResponseDTO);
    }
    @PostMapping("/record")
    public ResponseEntity<?> Record(@RequestBody RecorderDTO recorderDTO, @RequestHeader("Authorization")String authHeader){
        String token = authHeader.substring(7);
        JudgeInfo judgeInfo=authService.getJudgeInfo(token, recorderDTO.getJudgeCckId());
        recordService.updateRecordOne(recorderDTO.getAttemptIdx(), recorderDTO.getRecord(), judgeInfo);
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