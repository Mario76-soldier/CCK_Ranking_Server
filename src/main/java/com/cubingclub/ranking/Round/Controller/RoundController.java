package com.cubingclub.ranking.Round.Controller;

import com.cubingclub.ranking.Round.DTO.RoundCreateDTO;
import com.cubingclub.ranking.Round.DTO.RoundDTO;
import com.cubingclub.ranking.Round.DTO.RoundUpdateDTO;
import com.cubingclub.ranking.Round.Service.RoundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/round")
public class RoundController {
    private final RoundService roundService;

    @GetMapping("/past/{compIdx}")
    public ResponseEntity<Page<RoundDTO>> getRoundPast(Pageable pageable, @PathVariable(value="compIdx") Integer compIdx){
        return ResponseEntity.ok(roundService.getRoundPast(pageable, compIdx));
    }

    @GetMapping("/now/{compIdx}")
    public ResponseEntity<Page<RoundDTO>> getRoundNow(Pageable pageable, @PathVariable(value="compIdx") Integer compIdx){
        return ResponseEntity.ok(roundService.getRoundNow(pageable, compIdx));
    }

    @GetMapping("/future/{compIdx}")
    public ResponseEntity<Page<RoundDTO>> getRoundFuture(Pageable pageable, @PathVariable(value="compIdx") Integer compIdx){
        return ResponseEntity.ok(roundService.getRoundFuture(pageable, compIdx));
    }

    @GetMapping("/{roundIdx}")
    public ResponseEntity<RoundDTO> getRound(@PathVariable(value="roundIdx")Integer roundIdx){
        return ResponseEntity.ok(roundService.getOne(roundIdx));
    }

    @PostMapping("")
    public ResponseEntity<Void> createRound(@RequestBody List<RoundCreateDTO> roundList){
        roundService.createRoundList(roundList);
        return ResponseEntity.ok().build();
    }

    @PutMapping("")
    public ResponseEntity<Void> updateRound(@RequestBody List<RoundUpdateDTO> roundList){
        roundService.updateRoundList(roundList);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roundIdx}")
    public ResponseEntity<Void> deleteRound(@PathVariable(value="roundIdx")Integer roundIdx){
        roundService.deleteByRoundIdx(roundIdx);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/advance/{roundIdx}/{advance}")//어드밴스 인원 조정 콜
    public ResponseEntity<Void> updateAdvance(@PathVariable(value="roundIdx")Integer roundIdx, @PathVariable(value="advance")Integer advance) {
        roundService.updateAdvance(roundIdx, advance);
        return ResponseEntity.ok().build();
    }
}