package com.cubingclub.ranking.Record.Controller;

import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Competition.Service.CompetitionService;
import com.cubingclub.ranking.Record.Service.RecorderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recorder")
public class RecorderController {
    private final CompetitionService competitionService;
    private final RecorderService recorderService;
    private final AuthService authService;

    @GetMapping("/comp")
    public ResponseEntity<List<CompetitionDTO>> getCompNow(){
        return ResponseEntity.ok(competitionService.getCompNowNonPage());
    }

    @GetMapping("/info/{uid}")
    public ResponseEntity<PlayerInfo> getAccountInfo(@PathVariable String uid, @RequestHeader("Authorization")String authHeader){
        return ResponseEntity.ok(authService.getRFIDInfo(authHeader.substring(7), uid).orElseThrow(() -> new CustomizedException(ErrorCode.USER_NOT_FOUND)));
    }

    @GetMapping("/record/{cckId}/{compIdx}")
    public ResponseEntity<RecorderResponseDTO> getCurrentRecord(@PathVariable String cckId, @PathVariable Integer compIdx){
        return ResponseEntity.ok(recorderService.getForRecorder(cckId, compIdx));
    }

    @PatchMapping("/record")
    public ResponseEntity<SingleAverageDTO> updateAttempt(@RequestBody RecorderDTO recorderDTO){
        return ResponseEntity.ok(recorderService.updateAttempt(recorderDTO));
    }
}