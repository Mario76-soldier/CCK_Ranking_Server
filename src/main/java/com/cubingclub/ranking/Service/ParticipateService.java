package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.ParticipateDTO;
import com.cubingclub.ranking.DTO.ParticipateRequestDTO;
import com.cubingclub.ranking.DTO.RecorderResponseDTO;
import com.cubingclub.ranking.Repository.ParticipateRepository;
import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Repository.RoundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipateService {
    private final RoundRepository roundRepository;
    private final ParticipateRepository participateRepository;

    private static final BigDecimal DNF_TIME = BigDecimal.valueOf(-1);
    private static final BigDecimal DNS_TIME = BigDecimal.valueOf(-2);

    public List<Integer> getRank(Integer round) {
        return participateRepository.findByRoundOrder(round);
    }

    public ParticipateDTO getOne(Integer idx) {
        return new ParticipateDTO(participateRepository.findById(idx).orElseThrow(null));
    }

    public RecorderResponseDTO getForRecorder(String email, Integer compIdx){
        List<Participate> list=participateRepository.findByEmailAndRound_Comp_Idx(email, compIdx);
        for(Participate participate:list){
            if(participate.getFirst() == null || participate.getFirst().compareTo(BigDecimal.valueOf(0)) == 0){
                return new RecorderResponseDTO(participate.getIdx(), 1);
            }
            if(!Objects.equals(participate.getRound().getCubeEvent().getAvgCalc(), "single")) {
                if (participate.getSecond() == null || participate.getSecond().compareTo(BigDecimal.valueOf(0)) == 0) {
                    return new RecorderResponseDTO(participate.getIdx(), 2);
                }
                if (participate.getThird() == null || participate.getThird().compareTo(BigDecimal.valueOf(0)) == 0) {
                    return new RecorderResponseDTO(participate.getIdx(), 3);
                }
            }
            if(Objects.equals(participate.getRound().getCubeEvent().getAvgCalc(), "ao5")){
                if(participate.getFourth() == null || participate.getFourth().compareTo(BigDecimal.valueOf(0)) == 0){
                    return new RecorderResponseDTO(participate.getIdx(), 4);
                }
                if(participate.getFifth() == null || participate.getFifth().compareTo(BigDecimal.valueOf(0)) == 0){
                    return new RecorderResponseDTO(participate.getIdx(), 5);
                }
            }
        }
        return null;
    }

    public List<ParticipateDTO> getParticipate(Integer round) {
        List<Participate> list = participateRepository.findByRound(round);
        List<ParticipateDTO> listDTO = new ArrayList<>(list.size());
        list.forEach(participate -> listDTO.add(new ParticipateDTO(participate)));
        return listDTO;
    }

    private BigDecimal calculateAverage(BigDecimal[] record, int validTimes) {
        // 상위 validTimes개의 기록의 평균 계산
        return (record[validTimes - 1].add(record[validTimes - 2].add(record[validTimes - 3]))).divide(BigDecimal.valueOf(3),3, RoundingMode.HALF_DOWN);
        // 소수점 두 번째 자리까지 남기고 반내림
    }

    private boolean validateSeconds(BigDecimal[] times) {
        return Arrays.stream(times).allMatch(time -> time.compareTo(DNS_TIME) >= 0);
    }

    private Integer updateSinAvg(BigDecimal[] record, String calcType, Integer idx) {
        int validTimes = switch (calcType.toLowerCase()) {
            case "single" -> 1;
            case "mo3" -> 3;
            case "ao5" -> 4;
            default -> -1;
        };
        if (validTimes == -1) return -1;

        BigDecimal single = Arrays.stream(record)
                .filter(t -> t.compareTo(BigDecimal.ZERO) >= 0)
                .min(BigDecimal::compareTo)
                .orElse(Arrays.stream(record).filter(t -> t.compareTo(DNF_TIME) == 0 || t.compareTo(DNS_TIME) == 0)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO));
        int dnfCount = (int) Arrays.stream(record).filter(t -> t.equals(DNF_TIME)).count();
        int dnsCount = (int) Arrays.stream(record).filter(t -> t.equals(DNS_TIME)).count();
        BigDecimal average;

        if (("mo3".equalsIgnoreCase(calcType) || "ao5".equalsIgnoreCase(calcType)) && dnfCount + dnsCount < 1) {
            BigDecimal[] sortedRecord = Arrays.copyOf(record, record.length);
            Arrays.sort(sortedRecord);
            average = calculateAverage(sortedRecord, validTimes);
        } else if ("ao5".equalsIgnoreCase(calcType) && dnfCount + dnsCount < 2) {
            validTimes++;
            BigDecimal[] sortedRecord = Arrays.copyOf(record, record.length);
            Arrays.sort(sortedRecord);
            average = calculateAverage(sortedRecord, validTimes);
        } else {
            average = DNF_TIME;
        }
        if(Arrays.stream(record).anyMatch(r -> r.compareTo(BigDecimal.ZERO) == 0)) {
            average=BigDecimal.ZERO;
            single=BigDecimal.ZERO;
        }
        if (validateSeconds(record)) {
            participateRepository.updateRecord(
                    record[0],
                    record.length > 1 ? record[1] : BigDecimal.ZERO,
                    record.length > 2 ? record[2] : BigDecimal.ZERO,
                    record.length > 3 ? record[3] : BigDecimal.ZERO,
                    record.length > 4 ? record[4] : BigDecimal.ZERO,
                    average, single, idx
            );
            return idx;
        }
        return -1;
    }

    public Integer updateRecord(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth, BigDecimal fifth, String checkerName, Integer idx, String calcType) {
        BigDecimal[] record = switch (calcType.toLowerCase()) {
            case "single" -> new BigDecimal[]{first};
            case "mo3" -> new BigDecimal[]{first, second, third};
            case "ao5" -> new BigDecimal[]{first, second, third, fourth, fifth};
            default -> null;
        };
        if (record == null) {
            return -1;
        }
        int result = updateSinAvg(record, calcType, idx);
        if(Arrays.stream(record).allMatch(r -> r.compareTo(BigDecimal.ZERO) != 0)) {
            participateRepository.updateChecker(checkerName, idx);
            participateRepository.updateChecker2(null, idx);
        }
        else{
            participateRepository.updateChecker(null, idx);
            participateRepository.updateChecker2(null, idx);
        }
        return result;
    }

    public Integer updateRecordOne(Integer idx, Integer seq, BigDecimal record, String judgeEmail) {
        Participate participate = participateRepository.findByIdx(idx);
        String calcType = participate.getRound().getCubeEvent().getAvgCalc();
        ParticipateDTO participateDTO = new ParticipateDTO(participate);
        switch (seq) {
            case 1 -> {
                participateDTO.setFirst(record);
                participateDTO.setFirstJudge(judgeEmail);
            }
            case 2 -> {
                participateDTO.setSecond(record);
                participateDTO.setSecondJudge(judgeEmail);
            }
            case 3 -> {
                participateDTO.setThird(record);
                participateDTO.setThirdJudge(judgeEmail);
            }
            case 4 -> {
                participateDTO.setFourth(record);
                participateDTO.setFourthJudge(judgeEmail);
            }
            case 5 -> {
                participateDTO.setFifth(record);
                participateDTO.setFifthJudge(judgeEmail);
            }
        }
        participateRepository.save(participateDTO.toEntityFull());
        return (seq == 1 && "single".equalsIgnoreCase(calcType)) || (seq == 3 && "mo3".equalsIgnoreCase(calcType)) || (seq == 5 && "ao5".equalsIgnoreCase(calcType))
                ? updateSinAvg(new BigDecimal[]{participateDTO.getFirst(), participateDTO.getSecond(), participateDTO.getThird(), participateDTO.getFourth(), participateDTO.getFifth()}, calcType, idx)
                : idx;
    }

    public void deleteParticipate(Integer idx) {
        participateRepository.deleteByIdx(idx);
    }

    public void deleteByRoundIdx(Integer roundIdx) {
        participateRepository.deleteByRound_Idx(roundIdx);
    }

    public Integer addParticipate(ParticipateRequestDTO participate) {
        ParticipateDTO addPart = new ParticipateDTO(participate.getUserName(), participate.getPdx(), participate.getEmail(), roundRepository.findByIdx(participate.getRound()));
        return participateRepository.save(addPart.toEntity()).getIdx();
    }

    public void updateChecker(String checker, Integer idx) {
        participateRepository.updateChecker2(checker, idx);
    }

    public void updateRanking(Integer ranking, Integer idx) {
        participateRepository.updateRanking(ranking, idx);
    }
}
