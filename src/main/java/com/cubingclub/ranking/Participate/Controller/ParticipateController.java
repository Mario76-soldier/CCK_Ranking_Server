package com.cubingclub.ranking.Participate.Controller;

import com.cubingclub.ranking.Participate.DTO.ParticipateDTO;
import com.cubingclub.ranking.Participate.DTO.ParticipateRecordDTO;
import com.cubingclub.ranking.Participate.DTO.ParticipateRequestDTO;
import com.cubingclub.ranking.Participate.DTO.ParticipateUserDTO;
import com.cubingclub.ranking.Participate.Service.ParticipateService;
import com.cubingclub.ranking.Account.Service.AccountService;
import com.cubingclub.ranking.BestRecord.Service.PersonalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipateController {
    private final ParticipateService participateService;
    private final AccountService accountService;
    private final PersonalService personalService;

    @GetMapping("/records/{roundIdx}")
    public ResponseEntity<List<ParticipateUserDTO>> RecordList(@PathVariable(value="roundIdx") Integer roundIdx){
        return ResponseEntity.ok(participateService.getParticipateForUser(roundIdx));
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
    /**@PostMapping("/advance/{pastRoundIdx}/{advanceRoundIdx}")//기록에서 어드밴스 인원 다음 라운드로 넘기는 콜
    public ResponseEntity<Integer> Advance(@PathVariable(value="pastRoundIdx")Integer pastRoundIdx, @PathVariable(value="advanceRoundIdx")Integer advanceRoundIdx){
        return ResponseEntity.ok(participateService.addAdvance(pastRoundIdx, advanceRoundIdx));
    }**/

    @PostMapping("/participate")//선수 추가 콜
    public ResponseEntity<Integer> AddParticipate(@RequestBody List<ParticipateRequestDTO> participates){
        return ResponseEntity.ok(participateService.addParticipate(participates));
    }
}