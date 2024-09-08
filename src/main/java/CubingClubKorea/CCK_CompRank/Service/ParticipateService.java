package CubingClubKorea.CCK_CompRank.Service;


import CubingClubKorea.CCK_CompRank.DTO.ParticipateDTO;
import CubingClubKorea.CCK_CompRank.DTO.ParticipateRequestDTO;
import CubingClubKorea.CCK_CompRank.Repository.ParticipateRepository;
import CubingClubKorea.CCK_CompRank.Entity.Participate;
import CubingClubKorea.CCK_CompRank.Repository.RoundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipateService {
    private final RoundRepository roundRepository;
    private final ParticipateRepository participateRepository;

    private static final double MAX_TIME = 36000.0;
    private static final double DNF_TIME = -1;
    private static final double DNS_TIME = -2;

    public List<Integer> getRank(Integer round) {
        return participateRepository.findByRoundOrder(round);
    }

    public ParticipateDTO getOne(Integer idx) {
        return new ParticipateDTO(participateRepository.findById(idx).orElseThrow(null));
    }

    public List<ParticipateDTO> getParticipate(Integer round) {
        List<Participate> list = participateRepository.findByRound(round);
        List<ParticipateDTO> listDTO = new ArrayList<>();
        for (Participate participate : list) {
            listDTO.add(new ParticipateDTO(participate));
        }
        return listDTO;
    }

    private double calculateAverage(double[] record, int validTimes) {
        // 상위 validTimes개의 기록의 평균 계산
        double average = (record[validTimes - 1] + record[validTimes - 2] + record[validTimes - 3]) / 3.0;
        // 소수점 두 번째 자리까지 남기고 버림
        return Math.floor(average * 100) / 100;
    }

    private boolean validateSeconds(double[] times) {
        for (double time : times) {
            if (time >= -2) {
                continue;
            }
            return false;
        }
        return true;
    }

    public Integer updateRecord(Double first, Double second, Double third, Double fourth, Double fifth, String checkerName, Integer idx, String calcType) {
        double[] record;
        int validTimes;

        // calcType 에 따른 레코드 배열 크기 설정
        switch (calcType.toLowerCase()) {
            case "single":
                record = new double[]{first};
                validTimes = 1;
                break;
            case "mo3":
                record = new double[]{first, second, third};
                validTimes = 3;
                break;
            case "ao5":
                record = new double[]{first, second, third, fourth, fifth};
                validTimes = 4;
                break;
            default:
                return -1;  // 잘못된 입력
        }

        double single = Arrays.stream(record).filter(t -> t >= 0).min().orElse(Arrays.stream(record).filter(t -> t == -1 || t== -2).max().orElse(0));
        int dnfCounter = (int) Arrays.stream(record).filter(t -> t == DNF_TIME).count();
        int dnsCounter = (int) Arrays.stream(record).filter(t -> t == DNS_TIME).count();
        double average;

        if ((calcType.equalsIgnoreCase("mo3") && dnfCounter < 1) || (calcType.equalsIgnoreCase("ao5") && dnfCounter < 2)) {
            double[] sortedRecord = Arrays.copyOf(record, record.length);
            Arrays.sort(sortedRecord);
            average=calculateAverage(sortedRecord, validTimes);
        }
        else if((calcType.equalsIgnoreCase("mo3") && dnsCounter < 1) || (calcType.equalsIgnoreCase("ao5") && dnsCounter < 2)){
            average=-1;
        }
        else{
            average=-2;
        }
        // 시간 검증
        if (validateSeconds(record)) {
            // 저장 로직
            participateRepository.updateRecord(
                    first,
                    record.length > 1 ? second : 0,
                    record.length > 2 ? third : 0,
                    record.length > 3 ? fourth : 0,
                    record.length > 4 ? fifth : 0,
                    average, single,
                    checkerName, idx
            );
            return idx;
        } else {
            return -1;
        }
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
