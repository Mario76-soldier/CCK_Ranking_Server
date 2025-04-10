package com.cubingclub.ranking.Participate.Service;

import com.cubingclub.ranking.Participate.DTO.ParticipateDTO;
import com.cubingclub.ranking.Participate.DTO.ParticipateRecordDTO;
import com.cubingclub.ranking.Participate.DTO.ParticipateRequestDTO;
import com.cubingclub.ranking.Participate.DTO.RecorderResponseDTO;
import com.cubingclub.ranking.Participate.Entity.BestType;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Participate.Repository.ParticipateRepository;
import com.cubingclub.ranking.Participate.Entity.Participate;
import com.cubingclub.ranking.RFID.Repository.RFIDRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
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
    private final RFIDRepository rfidRepository;
    private final AccountRepository accountRepository;

    private static final BigDecimal DNF = BigDecimal.valueOf(-1);
    private static final BigDecimal DNS = BigDecimal.valueOf(-2);

    public List<ParticipateDTO> getParticipateForUser(Integer roundIdx) {
        List<Participate> list = participateRepository.findByRound_IdxForUser(roundIdx);
        List<ParticipateDTO> listDTO = new ArrayList<>(list.size());
        list.forEach(participate -> listDTO.add(new ParticipateDTO(participate)));
        return listDTO;
    }

    public RecorderResponseDTO getForRecorder(String uid, Integer compIdx) {
        String email;
        if((email=rfidRepository.findEmailByUid(uid))!=null) {
            List<Participate> list = participateRepository.findByAccount_EmailAndRound_Comp_Idx(email, compIdx);

            for (Participate p : list) {
                String name = p.getAccount().getEnName();
                String roundName = p.getRound().getCubeEvent().getEventName() + " " + p.getRound().getRoundName();
                String type = p.getRound().getCubeEvent().getAvgCalc();

                if (isEmpty(p.getFirst())) return new RecorderResponseDTO(p.getIdx(), 1, name, roundName);
                if (!"single".equals(type)) {
                    if (isEmpty(p.getSecond())) return new RecorderResponseDTO(p.getIdx(), 2, name, roundName);
                    if (isEmpty(p.getThird())) return new RecorderResponseDTO(p.getIdx(), 3, name, roundName);
                }
                if ("ao5".equals(type)) {
                    if (isEmpty(p.getFourth())) return new RecorderResponseDTO(p.getIdx(), 4, name, roundName);
                    if (isEmpty(p.getFifth())) return new RecorderResponseDTO(p.getIdx(), 5, name, roundName);
                }
            }
        }
        return null;
    }
    public void updateRecord(ParticipateRecordDTO p, String checkerName) {
        ParticipateDTO participateDTO = new ParticipateDTO(participateRepository.findByIdx(p.getIdx()));
        participateDTO.setFirst(p.getFirst());
        participateDTO.setSecond(p.getSecond());
        participateDTO.setThird(p.getThird());
        participateDTO.setFourth(p.getFourth());
        participateDTO.setFifth(p.getFifth());

        applyRecordUpdate(participateDTO, checkerName);
    }

    public void updateRecordOne(Integer idx, Integer seq, BigDecimal record, String judgeEmail) {
        Participate participate = participateRepository.findByIdx(idx);
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

        applyRecordUpdate(participateDTO, null); // 1차 체크 없음
    }

    private void applyRecordUpdate(ParticipateDTO participateDTO, String checker1Name) {
        String calcType = participateDTO.getRound().getCubeEvent().getAvgCalc();

        List<BigDecimal> times = Arrays.asList(
                participateDTO.getFirst(), participateDTO.getSecond(), participateDTO.getThird(),
                participateDTO.getFourth(), participateDTO.getFifth()
        );

        if (participateDTO.allNonZero()) {
            if (checker1Name != null) {
                participateDTO.setChecker1(checker1Name);
            }

            switch (calcType) {
                case "Ao5" -> {
                    participateDTO.setAverage(calculateAo5Average(times));
                    participateDTO.setSingle(calculateSingle(times));
                }
                case "Bo3" -> {
                    participateDTO.setAverage(calculateBo3Average(times.subList(0, 3)));
                    participateDTO.setSingle(calculateSingle(times.subList(0, 3)));
                }
                case "Single" -> participateDTO.setAverage(calculateSingle(times.subList(0, 1)));
            }
        } else {
            participateDTO.setAverage(BigDecimal.ZERO);
            participateDTO.setSingle(BigDecimal.ZERO);
            participateDTO.setRanking(0);
            participateDTO.setChecker1(null);
        }

        participateDTO.setChecker2(null);

        Map<String, BestType> bestTypeMap = calculateBestType(
                participateDTO.getAccount().getEmail(),
                participateDTO.getAccount().getState(),
                participateDTO.getRound().getCubeEvent().getEventName(),
                participateDTO.getSingle(),
                participateDTO.getAverage()
        );

        participateDTO.setSingleBestType(bestTypeMap.get("single"));
        participateDTO.setAverageBestType(bestTypeMap.get("average"));

        participateRepository.save(participateDTO.toEntityFull(accountRepository));
        participateRepository.setRankOnRound(participateDTO.getRound().getIdx());
    }


    public void deleteParticipate(Integer idx) {
        Integer roundIdx=participateRepository.findByIdx(idx).getRound().getIdx();
        participateRepository.deleteByIdx(idx);
        participateRepository.setRankOnRound(roundIdx);
    }

    public Integer addAdvance(Integer pastRoundIdx, Integer advanceRoundIdx){
        Round pastRound=roundRepository.findByIdx(pastRoundIdx);
        List<ParticipateDTO> participateList=getParticipateForUser(pastRoundIdx);
        List<Participate> advanceList=new ArrayList<>();
        for(int i=0; i<pastRound.getAdvance(); i++){
            ParticipateDTO advanceParticipate=participateList.get(i);
            advanceParticipate.setRound(roundRepository.findByIdx(advanceRoundIdx));
            advanceList.add(advanceParticipate.toEntity(accountRepository));
        }
        return participateRepository.saveAll(advanceList).size();
    }

    public Integer addParticipate(List<ParticipateRequestDTO> participates) {
        List<Participate> participateList=new ArrayList<>();
        for(ParticipateRequestDTO participateRequestDTO:participates){
            participateList.add(participateRequestDTO.toEntity(accountRepository, roundRepository));
        }
        return participateRepository.saveAll(participateList).size();
    }

    public void updateChecker(String checkerName, Integer idx){
        ParticipateDTO participateDTO=new ParticipateDTO(participateRepository.findByIdx(idx));
        participateDTO.setChecker2(checkerName);
        participateRepository.save(participateDTO.toEntityFull(accountRepository));
    }

    private static BigDecimal calculateAo5Average(List<BigDecimal> times) {
        List<BigDecimal> valid = new ArrayList<>();
        int penaltyCount = 0;

        for (BigDecimal t : times) {
            if (t.compareTo(BigDecimal.ZERO) < 0) penaltyCount++;
            else valid.add(t);
        }

        if (penaltyCount >= 2) return DNF;

        // 하나라도 DNF/DNS 있으면 Integer.MAX_VALUE처럼 처리
        while (valid.size() < 5) {
            valid.add(BigDecimal.valueOf(Long.MAX_VALUE));
        }

        valid.sort(Comparator.naturalOrder());
        List<BigDecimal> middleThree = valid.subList(1, 4);

        BigDecimal sum = middleThree.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateBo3Average(List<BigDecimal> times) {
        if (times.stream().anyMatch(t -> t.compareTo(BigDecimal.ZERO) < 0)) return DNF;

        BigDecimal sum = times.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(times.size()), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateSingle(List<BigDecimal> times) {
        return times.stream()
                .filter(t -> t.compareTo(BigDecimal.ZERO) >= 0)
                .min(Comparator.naturalOrder())
                .orElse(DNF);
    }


    private Map<String, BestType> calculateBestType(String email, String state, String eventName, BigDecimal single, BigDecimal average){
        Map<String, BestType> best=new HashMap<>();
        if(participateRepository.findPersonalSingleBest(email, eventName).compareTo(single)>0){
            if(participateRepository.findStateSingleBest(state, eventName).compareTo(single)>0){
                if(participateRepository.findAllSingleBest(eventName).compareTo(single)>0){
                    best.put("single", BestType.CCR);
                }
                else{
                    best.put("single", BestType.SR);
                }
            }
            else{
                best.put("single", BestType.PR);
            }
        }
        else{
            best.put("single",null);
        }
        if(participateRepository.findPersonalAverageBest(email, eventName).compareTo(average)>0){
            if(participateRepository.findStateAverageBest(state, eventName).compareTo(average)>0){
                if(participateRepository.findAllAverageBest(eventName).compareTo(average)>0){
                    best.put("average", BestType.CCR);
                }
                else{
                    best.put("average", BestType.SR);
                }
            }
            else{
                best.put("average", BestType.PR);
            }
        }
        else{
            best.put("average",null);
        }
        return best;
    }
    private boolean isEmpty(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }


}
