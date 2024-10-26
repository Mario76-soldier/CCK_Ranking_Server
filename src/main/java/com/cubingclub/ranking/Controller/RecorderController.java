package com.cubingclub.ranking.Controller;

import com.cubingclub.ranking.DTO.ParticipateDTO;
import com.cubingclub.ranking.DTO.RecorderDTO;
import com.cubingclub.ranking.DTO.RecorderResponseDTO;
import com.cubingclub.ranking.Service.CompetitionService;
import com.cubingclub.ranking.Service.ParticipateService;
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
    public ResponseEntity<?> CompIdx(){
        return ResponseEntity.ok(competitionService.getListToday(LocalDate.now()));
    }
    @GetMapping("/record/{email}/{idx}")
    public ResponseEntity<?> CurrentRecord(@PathVariable String email, @PathVariable Integer idx){
        RecorderResponseDTO recorderResponseDTO=participateService.getForRecorder(email,idx);
        if(recorderResponseDTO==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recorderResponseDTO);
    }

    @PostMapping("/record")
    public ResponseEntity<?> Record(@RequestBody RecorderDTO recordDTO){
        Integer idx=participateService.updateRecordOne(recordDTO.getIdx(), recordDTO.getSeq(), recordDTO.getRecord(), recordDTO.getJudgeEmail());
        ParticipateDTO participateDTO=participateService.getOne(idx);
        List<Integer> list=participateService.getRank(participateDTO.getRound().getIdx());
        for(int i=0; i<list.size(); i++) {
            participateService.updateRanking(i+1,list.get(i));
        }
        return ResponseEntity.ok(idx);
    }
}
