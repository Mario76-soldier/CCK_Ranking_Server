package com.cubingclub.ranking.Scrambler.Controller;

import com.cubingclub.ranking.Auth.DTO.JudgeInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Scrambler.DTO.ScramblerDTO;
import com.cubingclub.ranking.Scrambler.DTO.ScramblerResponseDTO;
import com.cubingclub.ranking.Scrambler.DTO.ScramblerUpdateDTO;
import com.cubingclub.ranking.Scrambler.Service.ScramblerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScramblerController {
    private final ScramblerService scramblerService;
    private final AuthService authService;
    @GetMapping("/scramble/check/{uid}")
    public ResponseEntity<JudgeInfo> GetAccountInfo(@PathVariable(name="uid")String uid, @RequestHeader("Authorization")String authHeader){
        String token = authHeader.substring(7);
        JudgeInfo judgeInfo=authService.getRFIDInfo(token, uid).orElse(null);
        if(judgeInfo==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }
    @GetMapping("/scrambles/{roundIdx}")
    public ResponseEntity<Map<String, List<ScramblerDTO>>> ScramblerList(@PathVariable(value="roundIdx")Integer roundIdx){
        return ResponseEntity.ok(scramblerService.getScrambleList(roundIdx));
    }
    @PostMapping("/scrambler")
    public ResponseEntity<?> SetScrambler(@RequestBody ScramblerUpdateDTO scramblerUpdateDTO, @RequestHeader("Authorization")String authHeader){
        String token = authHeader.substring(7);
        ScramblerResponseDTO scramblerDTO=scramblerService.getScramble(scramblerUpdateDTO.getCompIdx(), scramblerUpdateDTO.getPlayerUid(), token);
        if(scramblerDTO==null){
            return ResponseEntity.noContent().build();
        }
        int code=scramblerService.updateScrambler(scramblerDTO.getAttemptIdx(), scramblerUpdateDTO.getScramblerUid(), token);
        if(code==-1){
            return ResponseEntity.noContent().build();
        }
        else if(code==-2){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(scramblerDTO);
    }
}