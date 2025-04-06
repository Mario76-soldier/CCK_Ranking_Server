package com.cubingclub.ranking.CubeEvent.Controller;

import com.cubingclub.ranking.CubeEvent.Service.CubeEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CubeEventController {
    private final CubeEventService cubeEventService;
    @GetMapping("/event")
    public ResponseEntity<?> EventList(){
        return ResponseEntity.ok(cubeEventService.getCubeEvent());
    }
}