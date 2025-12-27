package com.cubingclub.ranking.Record.Controller;

import com.cubingclub.ranking.Auth.DTO.JudgeInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Record.Service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecordController {
    private final RecordService participateService;
    private final AuthService authService;
    @GetMapping("/records/{roundIdx}")
    public ResponseEntity<List<RecordDTO>> RecordList(@PathVariable(value="roundIdx") Integer roundIdx){
        return ResponseEntity.ok(participateService.getParticipate(roundIdx));
    }
    @PostMapping("/record")
    public ResponseEntity<?> UpdateRecord(@RequestBody ParticipateRecordDTO participateRecordDTO){
        participateService.updateRecord(participateRecordDTO);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/record/{idx}")
    public ResponseEntity<?> DeleteRecord(@PathVariable(value="idx")Integer idx){
        participateService.deleteParticipate(idx);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/record/quit")
    public ResponseEntity<?> QuitRecord(@RequestBody List<Integer> partList){
        participateService.quitParticipate(partList);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/advance/{pastRoundIdx}/{advanceRoundIdx}")//기록에서 어드밴스 인원 다음 라운드로 넘기는 콜
    public ResponseEntity<Integer> Advance(@PathVariable(value="pastRoundIdx")Integer pastRoundIdx, @PathVariable(value="advanceRoundIdx")Integer advanceRoundIdx){
        return ResponseEntity.ok(participateService.addAdvance(pastRoundIdx, advanceRoundIdx));
    }

    @PostMapping("/participate")//선수 추가 콜
    public ResponseEntity<Integer> AddParticipate(@RequestBody List<RecordRequestDTO> participates){
        return ResponseEntity.ok(participateService.addParticipate(participates));
    }

    @PostMapping("/extra")
    public ResponseEntity<?> AddExtra(@RequestBody ExtraDTO extraDTO, @RequestHeader("Authorization")String authHeader){
        String token = authHeader.substring(7);
        JudgeInfo judgeInfo=authService.getJudgeInfo(token);
        try{
            participateService.addExtra(extraDTO, judgeInfo);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }

    @DeleteMapping("/extra/{partIdx}/{extraNum}")
    public ResponseEntity<?> DeleteExtra(@PathVariable(name="partIdx")Integer partIdx, @PathVariable(name="extraNum")Integer extraNum){
        try{
            participateService.deleteExtra(partIdx, extraNum);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
}