package com.cubingclub.ranking.CubeEvent.Controller;

import com.cubingclub.ranking.CubeEvent.DTO.CubeEventDTO;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Service.CubeEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    @PostMapping("/event")
    public ResponseEntity<?> AdjustEvent(
            @RequestParam("EventName") String eventName,
            @RequestParam("AvgCalc") String avgCalc,
            @RequestParam("colorType") ColorType colorType,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        CubeEvent cubeEvent = cubeEventService.setCubeEvent(eventName, avgCalc, colorType, file);
        return ResponseEntity.ok(cubeEvent);
    }
}