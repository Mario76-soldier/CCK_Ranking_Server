package CubingClubKorea.CCK_CompRank.Service;


import CubingClubKorea.CCK_CompRank.DTO.ParticipateDTO;
import CubingClubKorea.CCK_CompRank.DTO.ParticipateRequestDTO;
import CubingClubKorea.CCK_CompRank.DTO.RecorderResponseDTO;
import CubingClubKorea.CCK_CompRank.Repository.ParticipateRepository;
import CubingClubKorea.CCK_CompRank.Entity.Participate;
import CubingClubKorea.CCK_CompRank.Repository.RoundRepository;
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
            if(participate.getFirst() == null || participate.getFirst().equals(BigDecimal.ZERO)){
                return new RecorderResponseDTO(participate.getRound().getIdx(), 1);
            }
            if(!Objects.equals(participate.getRound().getCubeEvent().getAvgCalc(), "single")) {
                if (participate.getSecond() == null || participate.getSecond().equals(BigDecimal.ZERO)) {
                    return new RecorderResponseDTO(participate.getRound().getIdx(), 2);
                }
                if (participate.getThird() == null || participate.getThird().equals(BigDecimal.ZERO)) {
                    return new RecorderResponseDTO(participate.getRound().getIdx(), 3);
                }
            }
            if(Objects.equals(participate.getRound().getCubeEvent().getAvgCalc(), "ao5")){
                if(participate.getFourth() == null || participate.getFourth().equals(BigDecimal.ZERO)){
                    return new RecorderResponseDTO(participate.getRound().getIdx(), 4);
                }
                if(participate.getFifth() == null || participate.getFifth().equals(BigDecimal.ZERO)){
                    return new RecorderResponseDTO(participate.getRound().getIdx(), 5);
                }
            }
        }
        return null;
    }

    public List<ParticipateDTO> getParticipate(Integer round) {
        List<Participate> list = participateRepository.findByRound(round);
        List<ParticipateDTO> listDTO = new ArrayList<>();
        for (Participate participate : list) {
            listDTO.add(new ParticipateDTO(participate));
        }
        return listDTO;
    }

    private BigDecimal calculateAverage(BigDecimal[] record, int validTimes) {
        // 상위 validTimes개의 기록의 평균 계산
        return (record[validTimes - 1].add(record[validTimes - 2].add(record[validTimes - 3]))).divide(BigDecimal.valueOf(3),3, RoundingMode.HALF_DOWN);
        // 소수점 두 번째 자리까지 남기고 반내림
    }

    private boolean validateSeconds(BigDecimal[] times) {
        for (BigDecimal time : times) {
            if (time.compareTo(BigDecimal.valueOf(-2))>=0) {
                continue;
            }
            return false;
        }
        return true;
    }

    public Integer updateRecord(BigDecimal first, BigDecimal second, BigDecimal third, BigDecimal fourth, BigDecimal fifth, String checkerName, Integer idx, String calcType) {
        BigDecimal[] record;
        int validTimes;

        // calcType 에 따른 레코드 배열 크기 설정
        switch (calcType.toLowerCase()) {
            case "single":
                record = new BigDecimal[]{first};
                validTimes = 1;
                break;
            case "mo3":
                record = new BigDecimal[]{first, second, third};
                validTimes = 3;
                break;
            case "ao5":
                record = new BigDecimal[]{first, second, third, fourth, fifth};
                validTimes = 4;
                break;
            default:
                return -1;  // 잘못된 입력
        }

        BigDecimal single = Arrays.stream(record).filter(t->t.compareTo(BigDecimal.valueOf(0))>=0).min(BigDecimal::compareTo).orElse(Arrays.stream(record).filter(t -> t.compareTo(BigDecimal.valueOf(-1))==0 || t.compareTo(BigDecimal.valueOf(-2))==0).max(BigDecimal::compareTo).orElse(BigDecimal.valueOf(0)));
        int dnfCounter = (int) Arrays.stream(record).filter(t -> t == DNF_TIME).count();
        int dnsCounter = (int) Arrays.stream(record).filter(t -> t == DNS_TIME).count();
        BigDecimal average;

        if ((calcType.equalsIgnoreCase("mo3") && dnfCounter+dnsCounter < 1) || (calcType.equalsIgnoreCase("ao5") && dnfCounter+dnsCounter < 1)) {
            BigDecimal[] sortedRecord = Arrays.copyOf(record, record.length);
            Arrays.sort(sortedRecord);
            average=calculateAverage(sortedRecord, validTimes);
        }
        else if ((calcType.equalsIgnoreCase("ao5") && dnfCounter+dnsCounter < 2)) {
            BigDecimal[] sortedRecord = Arrays.copyOf(record, record.length);
            validTimes++;
            Arrays.sort(sortedRecord);
            average=calculateAverage(sortedRecord, validTimes);
        }
        else{
            average= BigDecimal.valueOf(-1);
        }
        // 시간 검증
        if (validateSeconds(record)) {
            // 저장 로직
            participateRepository.updateRecord(
                    first,
                    record.length > 1 ? second : BigDecimal.valueOf(0),
                    record.length > 2 ? third : BigDecimal.valueOf(0),
                    record.length > 3 ? fourth : BigDecimal.valueOf(0),
                    record.length > 4 ? fifth : BigDecimal.valueOf(0),
                    average, single,
                    checkerName, idx
            );
            return idx;
        } else {
            return -1;
        }
    }

    public Integer updateRecordOne(Integer idx, Integer seq, BigDecimal record, String judgeEmail) {
        Participate participate=participateRepository.findByIdx(idx);
        ParticipateDTO participateDTO=new ParticipateDTO(participate);
        switch(seq){
            case 1:participateDTO.setFirst(record);
            case 2:participateDTO.setSecond(record);
            case 3:participateDTO.setThird(record);
            case 4:participateDTO.setFourth(record);
            case 5:participateDTO.setFifth(record);
        }
        return participateRepository.save(participateDTO.toEntity()).getIdx();
    }

    public void deleteParticipate(Integer idx) {
        participateRepository.deleteByIdx(idx);
    }

    public void deleteByRoundIdx(Integer roundIdx) {
        participateRepository.deleteByRound_Idx(roundIdx);
    }

    public Integer addParticipate(ParticipateRequestDTO participate) {
        ParticipateDTO addPart=new ParticipateDTO(participate.getUserName(), participate.getPdx(), participate.getEmail(), roundRepository.findByIdx(participate.getRound()));
        return participateRepository.save(addPart.toEntity()).getIdx();
    }

    public void updateChecker(String checker, Integer idx) {
        participateRepository.updateChecker2(checker, idx);
    }

    public void updateRanking(Integer ranking, Integer idx) {
        participateRepository.updateRanking(ranking, idx);
    }
}
