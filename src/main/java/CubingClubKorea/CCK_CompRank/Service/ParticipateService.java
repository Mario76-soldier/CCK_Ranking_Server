package CubingClubKorea.CCK_CompRank.Service;


import CubingClubKorea.CCK_CompRank.DTO.ParticipateDTO;
import CubingClubKorea.CCK_CompRank.Repository.ParticipateRepository;
import CubingClubKorea.CCK_CompRank.Entity.Participate;
import CubingClubKorea.CCK_CompRank.Repository.RoundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import CubingClubKorea.CCK_CompRank.Structure.Recorder;

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

    private double convertToSeconds(int time) {
        if (time == -1) return DNF_TIME;
        else if (time == -2) return DNS_TIME;

        int minutes = time / 10000;
        double seconds = (time % 10000) / 100.0;
        return minutes * 60 + seconds;
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

    public Integer updateRecord(Recorder recorder, String checkerName, Integer idx, String calcType) {
        double[] record;
        int validTimes;

        // calcType 에 따른 레코드 배열 크기 설정
        switch (calcType.toLowerCase()) {
            case "single":
                record = new double[]{convertToSeconds(recorder.getFirst())};
                validTimes = 1;
                break;
            case "mo3":
                record = new double[]{
                        convertToSeconds(recorder.getFirst()),
                        convertToSeconds(recorder.getSecond()),
                        convertToSeconds(recorder.getThird())
                };
                validTimes = 3;
                break;
            case "ao5":
                record = new double[]{
                        convertToSeconds(recorder.getFirst()),
                        convertToSeconds(recorder.getSecond()),
                        convertToSeconds(recorder.getThird()),
                        convertToSeconds(recorder.getFourth()),
                        convertToSeconds(recorder.getFifth())
                };
                validTimes = 4;
                break;
            default:
                return -1;  // 잘못된 입력
        }

        double single = Arrays.stream(record).filter(t -> t >= 0).min().orElse(Arrays.stream(record).filter(t -> t == -1 || t== -2).max().orElse(0));
        int dnfCounter = (int) Arrays.stream(record).filter(t -> t == DNF_TIME).count();
        int dnsCounter = (int) Arrays.stream(record).filter(t -> t == DNS_TIME).count();

        int avgM = 0;
        double avgS;
        if ((calcType.equalsIgnoreCase("mo3") && dnfCounter < 1) || (calcType.equalsIgnoreCase("ao5") && dnfCounter < 2)) {
            double[] sortedRecord = Arrays.copyOf(record, record.length);
            Arrays.sort(sortedRecord);
            double validAvg = calculateAverage(sortedRecord, validTimes);
            avgM = (int) validAvg / 60;
            avgS = ((validAvg % 60) * 100) / 100;
        }
        else if((calcType.equalsIgnoreCase("mo3") && dnsCounter < 1) || (calcType.equalsIgnoreCase("ao5") && dnsCounter < 2)){
            avgS=-1;
        }
        else{
            avgS=-2;
        }

        int singleM = (int) single / 60;
        double singleS = single % 60;
        // 시간 검증
        if (validateSeconds(record)) {
            // 저장 로직
            participateRepository.updateRecord(
                    (int) (record[0] / 60), record[0] % 60,
                    record.length > 1 ? (int) (record[1] / 60) : 0, record.length > 1 ? record[1] % 60 : 0,
                    record.length > 2 ? (int) (record[2] / 60) : 0, record.length > 2 ? record[2] % 60 : 0,
                    record.length > 3 ? (int) (record[3] / 60) : 0, record.length > 3 ? record[3] % 60 : 0,
                    record.length > 4 ? (int) (record[4] / 60) : 0, record.length > 4 ? record[4] % 60 : 0,
                    avgM, avgS,
                    singleM, singleS,
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

    public void deleteByRoundIdx(Integer idx) {
        participateRepository.deleteByRound(idx);
    }

    public Integer addParticipate(ParticipateDTO participate) {
        String event = roundRepository.findByIdx(participate.getRound()).getCubeEvent().getEventName();
        participate.setEventName(event);
        return participateRepository.save(participate.toEntity()).getIdx();
    }

    public void updateChecker(String checker, Integer idx) {
        participateRepository.updateChecker2(checker, idx);
    }

    public void updateRanking(Integer ranking, Integer idx) {
        participateRepository.updateRanking(ranking, idx);
    }
}
