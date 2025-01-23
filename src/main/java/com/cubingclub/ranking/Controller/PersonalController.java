package com.cubingclub.ranking.Controller;

import com.cubingclub.ranking.Service.PersonalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal")
public class PersonalController {
    private final PersonalService personalService;

    @GetMapping("/list/{email}")
    public ResponseEntity<?> PersonalRecordList(@PathVariable(name = "email")String email){
        return ResponseEntity.ok(personalService.getPersonalRecord(email));
    }
}
