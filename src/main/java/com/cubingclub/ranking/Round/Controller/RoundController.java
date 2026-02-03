package com.cubingclub.ranking.Round.Controller;

import com.cubingclub.ranking.Round.DTO.RoundCreateDTO;
import com.cubingclub.ranking.Round.DTO.RoundDTO;
import com.cubingclub.ranking.Round.DTO.RoundListDTO;
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

    @GetMapping("/{compIdx}/day-count/{dayCount}")
    public ResponseEntity<RoundListDTO> getRoundPast(@PathVariable Integer compIdx, @PathVariable Integer dayCount){
        return ResponseEntity.ok(roundService.getRoundList(compIdx, dayCount));
    }

    @GetMapping("/{roundIdx}")
    public ResponseEntity<RoundDTO> getRound(@PathVariable Integer roundIdx){
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
    public ResponseEntity<Void> deleteRound(@PathVariable Integer roundIdx){
        roundService.deleteByRoundIdx(roundIdx);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{roundIdx}/advance/{advance}")//어드밴스 인원 조정 콜
    public ResponseEntity<Void> updateAdvance(@PathVariable Integer roundIdx, @PathVariable Integer advance) {
        roundService.updateAdvance(roundIdx, advance);
        return ResponseEntity.ok().build();
    }
}