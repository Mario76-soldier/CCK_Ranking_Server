package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.DTO.RecorderDTO;
import CubingClubKorea.CCK_CompRank.DTO.RecorderResponseDTO;
import CubingClubKorea.CCK_CompRank.Service.CompetitionService;
import CubingClubKorea.CCK_CompRank.Service.ParticipateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    @GetMapping("/record")
    public ResponseEntity<?> CurrentRecord(String email, Integer compIdx){
        RecorderResponseDTO recorderResponseDTO=participateService.getForRecorder(email, compIdx);
        if(recorderResponseDTO==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(participateService.getForRecorder(email, compIdx));
    }

    @PostMapping("/record")
    public ResponseEntity<?> Record(@RequestBody RecorderDTO recordDTO){
        return ResponseEntity.ok(participateService.updateRecordOne(recordDTO.getIdx(), recordDTO.getSeq(), recordDTO.getRecord(), recordDTO.getJudgeEmail()));
    }
}
