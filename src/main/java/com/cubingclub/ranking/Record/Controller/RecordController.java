package com.cubingclub.ranking.Record.Controller;

import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Record.Service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {
    private final RecordService recordService;

    @GetMapping("/{roundIdx}")
    public ResponseEntity<List<RecordDTO>> getRecordList(@PathVariable Integer roundIdx){
        return ResponseEntity.ok(recordService.getRecordList(roundIdx));
    }

    @GetMapping("/break/{compIdx}")
    public ResponseEntity<List<RecordDTO>> getBreakRecordList(@PathVariable Integer compIdx){
        return ResponseEntity.ok(recordService.getBreakRecordList(compIdx));
    }

    @PostMapping("")
    public ResponseEntity<Integer> addPlayer(@RequestBody List<PlayerAddDTO> playerAddDTOList){
        recordService.addPlayer(playerAddDTOList);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("")
    public ResponseEntity<Void> updateRecord(@RequestBody RecordUpdateDTO playerAddDTO){
        recordService.updateRecord(playerAddDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idx}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Integer idx){
        recordService.deleteRecord(idx);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quit")
    public ResponseEntity<Void> quitRecord(@RequestBody List<Integer> partList){
        recordService.quitRecord(partList);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/advance")//기록에서 어드밴스 인원 다음 라운드로 넘기는 콜
    public ResponseEntity<Void> advancePlayer(@RequestBody AdvanceRequestDTO advanceRequestDTO){
        recordService.advancePlayer(advanceRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/extra")
    public ResponseEntity<Void> addExtra(@RequestBody ExtraDTO extraDTO, @RequestHeader("Authorization")String authHeader){
        recordService.addExtra(extraDTO, authHeader);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recordIdx}/extra/{extraNum}")
    public ResponseEntity<?> DeleteExtra(@PathVariable Integer recordIdx, @PathVariable Integer extraNum){
        recordService.deleteExtra(recordIdx, extraNum);
        return ResponseEntity.ok().build();
    }
}