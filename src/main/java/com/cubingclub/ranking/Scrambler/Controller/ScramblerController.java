package com.cubingclub.ranking.Scrambler.Controller;

import com.cubingclub.ranking.Auth.DTO.JwtToken;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Scrambler.DTO.ScrambleResponseDTO;
import com.cubingclub.ranking.Scrambler.DTO.ScramblerUpdateDTO;
import com.cubingclub.ranking.Scrambler.Service.ScramblerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/scramble")
public class ScramblerController {
    private final ScramblerService scramblerService;
    private final AuthService authService;

    @GetMapping("/check/{uid}")
    public ResponseEntity<Void> getAccountInfo(@PathVariable(name = "uid") String uid, @JwtToken String token) {
        authService.getRFIDInfo(token, uid).orElseThrow(() -> new CustomizedException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/scrambler")
    public ResponseEntity<ScrambleResponseDTO> updateScrambler(@RequestBody ScramblerUpdateDTO scramblerUpdateDTO, @JwtToken String token) {
        return ResponseEntity.ok(scramblerService.getScramble(scramblerUpdateDTO.getCompIdx(), scramblerUpdateDTO.getPlayerUid(), token));
    }
}