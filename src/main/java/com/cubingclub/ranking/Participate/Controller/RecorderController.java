package com.cubingclub.ranking.Participate.Controller;

import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Participate.DTO.ParticipateDTO;
import com.cubingclub.ranking.Participate.DTO.RecorderDTO;
import com.cubingclub.ranking.Participate.DTO.RecorderResponseDTO;
import com.cubingclub.ranking.Competition.Service.CompetitionService;
import com.cubingclub.ranking.Participate.DTO.SingleAverageDTO;
import com.cubingclub.ranking.Participate.Service.ParticipateService;
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
    @GetMapping("/comp")
    public ResponseEntity<List<CompetitionDTO>> CompIdx(){
        return ResponseEntity.ok(competitionService.getCompListToday(LocalDate.now()));
    }
    @GetMapping("/record/{uid}/{compIdx}")
    public ResponseEntity<RecorderResponseDTO> CurrentRecord(@PathVariable String uid, @PathVariable Integer compIdx){
        RecorderResponseDTO recorderResponseDTO=participateService.getForRecorder(uid, compIdx);
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
            singleAverageDTO.setSingle(participateDTO.getSingle());
            singleAverageDTO.setAverage(participateDTO.getAverage());
            return ResponseEntity.ok(singleAverageDTO);
        }
        else if(recordDTO.getSeq().equals(1) && eventName.equals("single")){
            singleAverageDTO.setSingle(participateDTO.getSingle());
            return ResponseEntity.ok(singleAverageDTO);
        }
        return ResponseEntity.ok().build();
    }
}