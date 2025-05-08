package com.cubingclub.ranking.Participate.Service;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Participate.DTO.*;
import com.cubingclub.ranking.Participate.Entity.BestType;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Participate.Repository.ParticipateRepository;
import com.cubingclub.ranking.Participate.Entity.Participate;
import com.cubingclub.ranking.RFID.Repository.RFIDRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
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

    public List<ParticipateUserDTO> getParticipateForUser(Integer roundIdx) {
        return participateRepository.findByRound_IdxForUser(roundIdx);
    }
    public ParticipateDTO getParticipateOne(Integer idx) {
        return new ParticipateDTO(participateRepository.findByIdx(idx));
    }

    public List<ParticipateDTO> getParticipateForAdmin(Integer roundIdx) {
        List<Participate> participateList=participateRepository.findByRound_Idx(roundIdx);
        List<ParticipateDTO> participateDTOList=new ArrayList<>();
        for(Participate participate:participateList){
            participateDTOList.add(new ParticipateDTO(participate));
        }
        return participateDTOList;
    }

    public RecorderResponseDTO getForRecorder(String uid, Integer compIdx) {
        String email;
        if((email=rfidRepository.findEmailByUid(uid))!=null) {
            List<Participate> list = participateRepository.findByAccount_EmailAndRound_Comp_Idx(email, compIdx);

            for (Participate p : list) {
                String name = p.getAccount().getEnName();
                String roundName = p.getRound().getCubeEvent().getEventName() + " " + p.getRound().getRoundName();
                String type = p.getRound().getCubeEvent().getAvgCalc();
                String state=p.getAccount().getState();

                if (isEmpty(p.getFirst())) return new RecorderResponseDTO(p.getIdx(), 1, name, roundName, state);
                if (!"single".equals(type)) {
                    if (isEmpty(p.getSecond())) return new RecorderResponseDTO(p.getIdx(), 2, name, roundName, state);
                    if (isEmpty(p.getThird())) return new RecorderResponseDTO(p.getIdx(), 3, name, roundName, state);
                }
                if ("ao5".equals(type)) {
                    if (isEmpty(p.getFourth())) return new RecorderResponseDTO(p.getIdx(), 4, name, roundName, state);
                    if (isEmpty(p.getFifth())) return new RecorderResponseDTO(p.getIdx(), 5, name, roundName, state);
                }
            }
        }
        return null;
    }
    public void updateRecord(ParticipateRecordDTO p) {
        ParticipateDTO participateDTO = new ParticipateDTO(participateRepository.findByIdx(p.getIdx()));
        participateDTO.setFirst(p.getFirst());
        participateDTO.setSecond(p.getSecond());
        participateDTO.setThird(p.getThird());
        participateDTO.setFourth(p.getFourth());
        participateDTO.setFifth(p.getFifth());
        applyRecordUpdate(participateDTO);
    }

    public void updateRecordOne(Integer idx, Integer seq, BigDecimal record, String judgeEmail) {
        Participate participate = participateRepository.findByIdx(idx);
        ParticipateDTO participateDTO = new ParticipateDTO(participate);

        switch (seq) {
            case 1 -> {
                participateDTO.setFirst(record);
                participateDTO.setFirstJudge(new AccountDTO(accountRepository.findByEmail(judgeEmail).orElseThrow()));
            }
            case 2 -> {
                participateDTO.setSecond(record);
                participateDTO.setSecondJudge(new AccountDTO(accountRepository.findByEmail(judgeEmail).orElseThrow()));
            }
            case 3 -> {
                participateDTO.setThird(record);
                participateDTO.setThirdJudge(new AccountDTO(accountRepository.findByEmail(judgeEmail).orElseThrow()));
            }
            case 4 -> {
                participateDTO.setFourth(record);
                participateDTO.setFourthJudge(new AccountDTO(accountRepository.findByEmail(judgeEmail).orElseThrow()));
            }
            case 5 -> {
                participateDTO.setFifth(record);
                participateDTO.setFifthJudge(new AccountDTO(accountRepository.findByEmail(judgeEmail).orElseThrow()));
            }
        }
        applyRecordUpdate(participateDTO); // 1차 체크 없음
    }

    public void addExtra(ExtraDTO extraDTO, AccountDTO delegate){

        ParticipateDTO participateDTO=new ParticipateDTO(participateRepository.findByIdx(extraDTO.getPartIdx()));
        switch (extraDTO.getExtraNum()){
            case 1->{
                participateDTO.setExtra1Delegate(delegate);
                participateDTO.setExtra1(extraDTO.getExtraTry());
                participateDTO.setExtra1Reason(extraDTO.getExtraReason());
            }
            case 2->{
                participateDTO.setExtra2Delegate(delegate);
                participateDTO.setExtra2(extraDTO.getExtraTry());
                participateDTO.setExtra2Reason(extraDTO.getExtraReason());
            }
        };
        participateRepository.save(participateDTO.toEntityFull(accountRepository));
    }
    public void deleteExtra(Integer partIdx, Integer extraNum){

        ParticipateDTO participateDTO=new ParticipateDTO(participateRepository.findByIdx(partIdx));
        switch (extraNum){
            case 1->{
                participateDTO.setExtra1Delegate(null);
                participateDTO.setExtra1(null);
                participateDTO.setExtra1Reason(null);
            }
            case 2->{
                participateDTO.setExtra2Delegate(null);
                participateDTO.setExtra2(null);
                participateDTO.setExtra2Reason(null);
            }
        };
        participateRepository.save(participateDTO.toEntityFull(accountRepository));
    }

    public ExpectedBestWorstDTO calculateExpectation(Integer idx){
        Participate participate=participateRepository.findByIdx(idx);
        if(!participate.getRound().getCubeEvent().getAvgCalc().equals("ao5")){
            return null;
        }
        ParticipateDTO participateDTO=new ParticipateDTO(participate);
        if(participateDTO.getThird()!=null && !participateDTO.getThird().equals(BigDecimal.ZERO)){
            List<BigDecimal> best = Arrays.asList(
                    participateDTO.getFirst(), participateDTO.getSecond(), participateDTO.getThird(),
                    participateDTO.getFourth(), BigDecimal.valueOf(0.01)
            );
            List<BigDecimal> worst = Arrays.asList(
                    participateDTO.getFirst(), participateDTO.getSecond(), participateDTO.getThird(),
                    participateDTO.getFourth(), BigDecimal.valueOf(-1)
            );
            ExpectedBestWorstDTO expectedBestWorstDTO=new ExpectedBestWorstDTO();
            expectedBestWorstDTO.setBestAverage(calculateAo5Average(best));
            expectedBestWorstDTO.setWorstAverage(calculateAo5Average(worst));
            return expectedBestWorstDTO;
        }
        return null;
    }

    private void applyRecordUpdate(ParticipateDTO participateDTO) {
        String calcType = participateDTO.getRound().getCubeEvent().getAvgCalc();

        List<BigDecimal> times = Arrays.asList(
                participateDTO.getFirst(), participateDTO.getSecond(), participateDTO.getThird(),
                participateDTO.getFourth(), participateDTO.getFifth()
        );
        if (participateDTO.allNonZero()) {
            switch (calcType) {
                case "ao5" -> {
                    participateDTO.setAverage(calculateAo5Average(times));
                    participateDTO.setSingle(calculateSingle(times));
                }
                case "bo3" -> {
                    participateDTO.setAverage(calculateBo3Average(times.subList(0, 3)));
                    participateDTO.setSingle(calculateSingle(times.subList(0, 3)));
                }
                case "single" -> participateDTO.setAverage(calculateSingle(times.subList(0, 1)));
            }
        } else {
            participateDTO.setAverage(BigDecimal.ZERO);
            participateDTO.setSingle(BigDecimal.ZERO);
            participateDTO.setRanking(null);
        }

        participateRepository.save(participateDTO.toEntityFull(accountRepository));
        participateRepository.setRankOnRound(participateDTO.getRound().getIdx());
        List<Participate> participateList=participateRepository.findByRound_IdxForBestType(participateDTO.getRound().getIdx());
        for(Participate participate: participateList) {
            if(!participate.getIdx().equals(participateDTO.getIdx()) && (participate.getSingleBestType()==null || participate.getSingleBestType()==BestType.PR) && (participate.getAverageBestType()==null || participate.getAverageBestType()==BestType.PR)){
                continue;
            }
            ParticipateDTO eachParticipateDTO=new ParticipateDTO(participate);
            Map<String, BestType> bestTypeMap = calculateBestType(
                    participate.getIdx(),
                    participate.getAccount().getEmail(),
                    participate.getAccount().getState(),
                    participate.getRound().getCubeEvent().getEventName(),
                    participate.getSingle(),
                    participate.getAverage()
            );
            eachParticipateDTO.setSingleBestType(bestTypeMap.get("single"));
            eachParticipateDTO.setAverageBestType(bestTypeMap.get("average"));
            participateRepository.save(eachParticipateDTO.toEntityFull(accountRepository));
        }
    }


    public void deleteParticipate(Integer idx) {
        Integer roundIdx=participateRepository.findByIdx(idx).getRound().getIdx();
        participateRepository.deleteByIdx(idx);
        participateRepository.setRankOnRound(roundIdx);
    }

    public void quitParticipate(List<Integer> partList) {
        for(Integer part:partList){
            participateRepository.deleteByIdx(part);
        }
    }

    /**public Integer addAdvance(Integer pastRoundIdx, Integer advanceRoundIdx){
        Round pastRound=roundRepository.findByIdx(pastRoundIdx);
        List<ParticipateUserDTO> participateList=getParticipateForUser(pastRoundIdx);
        List<Participate> advanceList=new ArrayList<>();
        for(int i=0; i<pastRound.getAdvance(); i++){
            ParticipateDTO advanceParticipate=participateList.get(i);
            advanceParticipate.setRound(roundRepository.findByIdx(advanceRoundIdx));
            advanceList.add(advanceParticipate.toEntity(accountRepository));
        }
        return participateRepository.saveAll(advanceList).size();
    }**/

    public Integer addParticipate(List<ParticipateRequestDTO> participates) {
        List<Participate> participateList=new ArrayList<>();
        for(ParticipateRequestDTO participateRequestDTO:participates){
            participateList.add(participateRequestDTO.toEntity(accountRepository, roundRepository));
        }
        return participateRepository.saveAll(participateList).size();
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


    private Map<String, BestType> calculateBestType(Integer idx, String email, String state, String eventName, BigDecimal single, BigDecimal average){
        Map<String, BestType> best=new HashMap<>();
        BestResult personalSingleBest=participateRepository.findPersonalSingleBest(email, eventName);
        if(personalSingleBest==null || personalSingleBest.getBest().compareTo(single)>0 || (personalSingleBest.getIdx().equals(idx) && personalSingleBest.getBest().compareTo(single)>=0)){
            BestResult stateSingleBest=participateRepository.findStateSingleBest(state, eventName);
            if(stateSingleBest==null || stateSingleBest.getBest().compareTo(single)>0 || (stateSingleBest.getIdx().equals(idx) && stateSingleBest.getBest().compareTo(single)>=0)){
                BestResult allSingleBest=participateRepository.findAllSingleBest(eventName);
                if(allSingleBest==null || allSingleBest.getBest().compareTo(single)>0 || (allSingleBest.getIdx().equals(idx) && allSingleBest.getBest().compareTo(single)>=0)){
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
        BestResult personalAverageBest=participateRepository.findPersonalAverageBest(email, eventName);
        if(personalAverageBest==null || personalAverageBest.getBest().compareTo(average)>0 || (personalAverageBest.getIdx().equals(idx) && personalAverageBest.getBest().compareTo(single)>=0)){
            BestResult stateAverageBest=participateRepository.findStateAverageBest(state, eventName);
            if(stateAverageBest==null || stateAverageBest.getBest().compareTo(average)>0 || (stateAverageBest.getIdx().equals(idx) && stateAverageBest.getBest().compareTo(single)>=0)){
                BestResult allAverageBest=participateRepository.findAllAverageBest(eventName);
                if(allAverageBest==null || allAverageBest.getBest().compareTo(average)>0 || (allAverageBest.getIdx().equals(idx) && allAverageBest.getBest().compareTo(single)>=0)){
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
