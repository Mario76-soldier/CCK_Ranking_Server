package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecorderService {
    private final AuthService authService;
    private final RecordRepository recordRepository;
    private final AttemptRepository attemptRepository;
    private final RecordService recordService;
    private final RecordCalculator recordCalculator;

    public RecorderResponseDTO getForRecorder(String cckId, Integer compIdx) {
        List<Record> list = recordRepository.findByCckIdAndRound_Comp_IdxOrderByRound_EventStart(cckId, compIdx);
        // OneToMany 컬렉션 초기화
        list.forEach(record -> Hibernate.initialize(record.getAttempts()));
        for (Record p : list) {
            String roundName = p.getRound().getCubeEvent().getEventName() + " " + p.getRound().getRoundName();
            List<Attempt> attemptList=p.getAttempts();
            log.info(p.getEnName());

            for(Attempt attempt: attemptList){
                if(attempt.getTime()==null || attempt.getTime().compareTo(BigDecimal.ZERO)==0){
                    return new RecorderResponseDTO(attempt.getIdx(), attempt.getAttemptTry(), p.getName(), p.getEnName(), p.getState(), roundName, attempt.getExtra());
                }
            }
        }
        throw new CustomizedException(ErrorCode.RECORDER_RECORD_NOT_REMAIN);
    }

    public SingleAverageDTO updateAttempt(RecorderDTO recorderDTO){
        PlayerInfo playerInfo=authService.getPlayerInfo(recorderDTO.getJudgeCckId());
        updateRecordOne(recorderDTO.getAttemptIdx(), recorderDTO.getRecord(), playerInfo);
        Attempt attempt=attemptRepository.findById(recorderDTO.getAttemptIdx()).orElseThrow(()->new CustomizedException(ErrorCode.RECORD_ATTEMPT_NOT_FOUND));
        Record record=attempt.getRecord();
        String avgCalc=record.getRound().getCubeEvent().getAvgCalc();
        if(attempt.getAttemptTry().equals(1) && avgCalc.equals("single")){
            return new SingleAverageDTO("result", recordCalculator.formatTime(record.getSingle()), null);
        }
        int maxCount = Integer.parseInt(avgCalc.substring(2));
        if(attempt.getAttemptTry().equals(maxCount-1) && avgCalc.startsWith("ao")){
            return calculateExpectation(record.getIdx());
        }
        if (attempt.getAttemptTry().equals(maxCount)){
            return new SingleAverageDTO("result", recordCalculator.formatTime(record.getSingle()), recordCalculator.formatTime(record.getAverage()));
        }
        return new SingleAverageDTO("processing", null, null);
    }

    private void updateRecordOne(Integer idx, BigDecimal record, PlayerInfo judgeInfo) {
        Attempt lastAttempt = attemptRepository.findByIdx(idx);
        lastAttempt.updateTime(record);
        lastAttempt.updateJudge(judgeInfo.getName(), judgeInfo.getCckId());
        Record record1 = recordRepository.findByIdx(lastAttempt.getRecord().getIdx());
        recordService.applyRecordUpdate(record1);
    }

    private SingleAverageDTO calculateExpectation(Integer idx) {
        Record record = recordRepository.findByIdx(idx);
        // OneToMany 컬렉션 초기화
        Hibernate.initialize(record.getAttempts());
        String avgCalc = record.getRound().getCubeEvent().getAvgCalc();

        // "ao" 로 시작하지 않으면 null 리턴
        if (!avgCalc.startsWith("ao")) {
            log.info("not ao");
            return null;
        }

        // "ao" 뒤 숫자 추출
        int aoCount = Integer.parseInt(avgCalc.substring(2)); // "ao12" → 12

        List<Attempt> attemptList = record.getAttempts();
        if (attemptList.size() < aoCount) {
            return null; // 시도 개수가 부족하면 계산 불가
        }

        // 아직 시도 중이면 기대값 계산
        // 마지막 시도가 비어 있거나 0보다 크면
        Attempt lastAttempt = attemptList.get(Math.min(aoCount - 2, attemptList.size() - 2));
        if (lastAttempt.getTime() == null || lastAttempt.getTime().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        // Best/Worst 가정용 리스트 구성
        List<BigDecimal> bestList = new ArrayList<>();
        List<BigDecimal> worstList = new ArrayList<>();

        for (int i = 0; i < aoCount - 1; i++) {
            bestList.add(attemptList.get(i).getTime());
            worstList.add(attemptList.get(i).getTime());
        }

        // 남은 한 시도를 최소/최대 값으로 가정
        bestList.add(BigDecimal.valueOf(0.01));  // 매우 좋은 기록
        worstList.add(BigDecimal.valueOf(-1));  // 매우 나쁜 기록

        // 평균 계산
        SingleAverageDTO expectedDTO = new SingleAverageDTO();
        expectedDTO.setSingle(recordCalculator.formatTime(recordCalculator.calculateAoXAverage(bestList)));
        expectedDTO.setAverage(recordCalculator.formatTime(recordCalculator.calculateAoXAverage(worstList)));
        expectedDTO.setType("bwpa");
        return expectedDTO;
    }
}
