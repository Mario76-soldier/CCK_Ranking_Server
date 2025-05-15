package com.cubingclub.ranking.Scrambler.Controller;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.Service.AccountService;
import com.cubingclub.ranking.Participate.DTO.*;
import com.cubingclub.ranking.Participate.Service.ParticipateService;
import com.cubingclub.ranking.Scrambler.DTO.ScramblerDTO;
import com.cubingclub.ranking.Scrambler.Service.ScramblerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScramblerController {
    private final ScramblerService scramblerService;
    @GetMapping("/scramble/{compIdx}/{uid}")
    public ResponseEntity<ScramblerDTO> PlayerScramble(@PathVariable(value="compIdx")Integer compIdx, @PathVariable(value="uid")String uid){
        return ResponseEntity.ok(scramblerService.getScramble(compIdx, uid));
    }
    @GetMapping("/scrambles/{roundIdx}")
    public ResponseEntity<Map<String, List<ScramblerDTO>>> ScramblerList(@PathVariable(value="roundIdx")Integer roundIdx){
        return ResponseEntity.ok(scramblerService.getScrambleList(roundIdx));
    }
}