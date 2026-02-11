package com.cubingclub.ranking.Record.Repository;

import com.cubingclub.ranking.Record.DTO.PlayerCountStatDTO;
import com.cubingclub.ranking.Record.DTO.StaffCountStatDTO;
import com.cubingclub.ranking.Record.Entity.Record;

import java.util.List;

public interface PlayerStatRepository {
    List<Record> findBreakRecordByCckId(String cckId);
    List<Record> findHistoryByCckIdAndEventName(String cckId, String eventName);
    PlayerCountStatDTO findPlayerCountByCckId(String cckId);
    StaffCountStatDTO findStaffCountByCckId(String cckId);
}
