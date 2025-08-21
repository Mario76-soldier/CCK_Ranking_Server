package com.cubingclub.ranking.PersonalRecord.Controller;

import com.cubingclub.ranking.PersonalRecord.Service.PersonalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal")
public class PersonalRecordController {
    private final PersonalRecordService personalRecordService;
    @GetMapping("/{cckId}")
    public ResponseEntity<?> BestRecordList(@PathVariable(name = "cckId")String cckId){
        return ResponseEntity.ok(personalRecordService.getPersonalRecord(cckId));
    }
}
