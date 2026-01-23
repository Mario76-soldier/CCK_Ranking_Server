package com.cubingclub.ranking.CubeEvent.Controller;

import com.cubingclub.ranking.CubeEvent.DTO.CubeEventDTO;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Service.CubeEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class CubeEventController {
    private final CubeEventService cubeEventService;

    @GetMapping("")
    public ResponseEntity<List<CubeEventDTO>> getEventList(){
        return ResponseEntity.ok(cubeEventService.getCubeEvent());
    }

    @PostMapping("")
    public ResponseEntity<Void> addEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam("avgCalc") String avgCalc,
            @RequestParam("colorType") ColorType colorType,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        cubeEventService.createCubeEvent(eventName, avgCalc, colorType, file);
        return ResponseEntity.ok().build();
    }
}