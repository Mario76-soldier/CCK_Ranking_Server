package com.cubingclub.ranking.Round.Controller;

import com.cubingclub.ranking.Round.DTO.CreateRoundDTO;
import com.cubingclub.ranking.Round.DTO.RoundDTO;
import com.cubingclub.ranking.Round.Service.RoundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoundController {
    private final RoundService roundService;
    @GetMapping("/rounds/{compIdx}")
    public ResponseEntity<Map<String, List<RoundDTO>>> RoundList(@PathVariable(value="compIdx") Integer compIdx){
        LocalDateTime now=LocalDateTime.now();
        return ResponseEntity.ok(roundService.getRoundList(compIdx, now));
    }
    @GetMapping("/round/{roundIdx}")
    public ResponseEntity<RoundDTO> RoundInfo(@PathVariable(value="roundIdx")Integer roundIdx){
        return ResponseEntity.ok(roundService.getOne(roundIdx));
    }
    @PostMapping("/round")
    public ResponseEntity<List<Integer>> CreateRound(@RequestBody List<CreateRoundDTO> roundList){
        return ResponseEntity.ok(roundService.create(roundList));
    }
    @DeleteMapping("/round/{roundIdx}")
    public ResponseEntity<?> DeleteRound(@PathVariable(value="roundIdx")Integer roundIdx){
        roundService.deleteByRoundIdx(roundIdx);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/advance/{roundIdx}/{advance}")//어드밴스 인원 조정 콜
    public ResponseEntity<Integer> UpdateAdvance(@PathVariable(value="roundIdx")Integer roundIdx, @PathVariable(value="advance")Integer advance) {
        roundService.updateAdvance(roundIdx, advance);
        return ResponseEntity.ok(roundService.getOne(roundIdx).getAdvance());
    }
}