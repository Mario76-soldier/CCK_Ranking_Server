package com.cubingclub.ranking.Scrambler.Service;

import com.cubingclub.ranking.Participate.DTO.ParticipateDTO;
import com.cubingclub.ranking.Participate.DTO.RecorderResponseDTO;
import com.cubingclub.ranking.Participate.Entity.Participate;
import com.cubingclub.ranking.Participate.Service.ParticipateService;
import com.cubingclub.ranking.RFID.Service.RFIDService;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Scrambler.DTO.ScramblerDTO;
import com.cubingclub.ranking.Scrambler.Entity.Scrambler;
import com.cubingclub.ranking.Scrambler.Repository.ScramblerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ScramblerService {
    private final RoundRepository roundRepository;
    private final ScramblerRepository scramblerRepository;
    private final WebClient webClient;
    private final ParticipateService participateService;
    private final Map<String, String> cube=Map.of(
            "B","#0000ff",
            "R","#ff0000",
            "D","#ffff00",
            "U","#ffffff",
            "F","#00ff00",
            "L","#ff8000"
    );
    private final Map<String, String> tetra=Map.of(
            "R", "#0000ff",
            "D", "#ffff00",
            "L", "#ff0000",
            "F", "#00ff00"
    );
    private final Map<String, String> dode = new HashMap<>() {{
        put("B", "#71e600");
        put("D", "#999999");
        put("F", "#006600");
        put("DL", "#88ddff");
        put("BL", "#ffcc00");
        put("L", "#8a1aff");
        put("DR", "#ffffb3");
        put("DBL", "#ff8433");
        put("BR", "#0000b3");
        put("R", "#dd0000");
        put("U", "#ffffff");
        put("DBR", "#ff99ff");
    }};
    private final Map<String, String> skewb=Map.of(
            "R", "#0000ff",
            "B", "#ff8000",
            "D", "#ffff00",
            "U", "#ffffff",
            "F", "#ff0000",
            "L", "#00ff00"
    );
    private final Map<String, String> clock = new HashMap<>() {{
        put("FrontTopClock", "#ffcc44");
        put("FrontHandBorder", "#113366");
        put("Front", "#113366");
        put("BackPin", "#446699");
        put("BackHand", "#ccddee");
        put("FrontPin", "#88aacc");
        put("BackTopClock", "#cc6600");
        put("FrontHand", "#113366");
        put("FrontClock", "#ccddee");
        put("BackHandBorder", "#ccddee");
        put("Back", "#ccddee");
        put("BackClock", "#113366");
    }};
    private final Map<String, String> square=Map.of(
            "B", "#ff8000",
            "R", "#00ff00",
            "D", "#ffffff",
            "U", "#ffff00",
            "F", "#ff0000",
            "L", "#0000ff"
    );

    public void saveScramble(Integer roundIdx, List<String> groups) {
        Round round=roundRepository.findByIdx(roundIdx);
        Map<String, String> body;
        String puzzleId;
        int count=0;
        switch (round.getCubeEvent().getColorType()){
            case cube-> {
                body = cube;
            }
            case tetra-> {
                body = tetra;
            }
            case dode-> {
                body = dode;
            }
            case skewb-> {
                body = skewb;
            }
            case clock-> {
                body = clock;
            }
            case square-> {
                body = square;
            }
            default -> {
                body = null;
            }
        }
        switch (round.getCubeEvent().getEventName()){
            case "2x2x2"->{
                puzzleId="222";
            }
            case "3x3x3"->{
                puzzleId="333";
            }
            case "4x4x4"->{
                puzzleId="444";
            }
            case "5x5x5"->{
                puzzleId="555";
            }
            case "6x6x6"->{
                puzzleId="666";
            }
            case "7x7x7"->{
                puzzleId="777";
            }
            case "3x3x3 BLD"->{
                puzzleId="333bf";
            }
            case "3x3x3 FMC"->{
                puzzleId="333fm";
            }
            case "3x3x3 OH"->{
                puzzleId="333oh";
            }
            case "Clock"->{
                puzzleId="clock";
            }
            case "Megaminx"->{
                puzzleId="minx";
            }
            case "Pyraminx"->{
                puzzleId="pyram";
            }
            case "Skewb"->{
                puzzleId="skewb";
            }
            case "Square-1"->{
                puzzleId="sq1";
            }
            case "4x4x4 BLD"->{
                puzzleId="444bf";
            }
            case "5x5x5 BLD"->{
                puzzleId="555bf";
            }
            case "3x3x3 MULTBLD"->{
                puzzleId="333mbf";
            }
            default ->{
                puzzleId=null;
            }
        }
        switch (round.getCubeEvent().getAvgCalc()){
            case "ao5"->{
                count=5;
            }
            case "mo3"->{
                count=3;
            }
            case "single"->{
                count=1;
            }
        }
        if(puzzleId==null || body==null){
            return;
        }
        ScramblerDTO scramblerDTO = new ScramblerDTO();
        scramblerDTO.setRound(round);
        String svg;
        for(String group:groups) {
            if(!scramblerRepository.findByRound_IdxAndRoundGroup(roundIdx, group).isEmpty()){
                continue;
            }
            scramblerDTO.setRoundGroup(group);
            for (int i = 1; i <= count; i++) {
                JsonNode response = webClient.post()
                        .uri("/frontend/puzzle/" + puzzleId + "/scramble")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();
                scramblerDTO.setSeq(String.valueOf(i));
                scramblerDTO.setScramble(response.get("scramble").asText());
                svg=response.get("svgImage").asText();
                svg = svg.replaceFirst("<svg([^>]*)\\swidth=\"[^\"]+\"", "<svg$1");
                svg = svg.replaceFirst("<svg([^>]*)\\sheight=\"[^\"]+\"", "<svg$1");
                scramblerDTO.setSvg(svg);
                scramblerRepository.save(scramblerDTO.toEntity());
            }
            for (int i = 1; i <= 2; i++) {
                JsonNode response = webClient.post()
                        .uri("/frontend/puzzle/" + puzzleId + "/scramble")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();
                scramblerDTO.setSeq("ex"+i);
                scramblerDTO.setScramble(response.get("scramble").asText());
                svg=response.get("svgImage").asText();
                svg = svg.replaceFirst("<svg([^>]*)\\swidth=\"[^\"]+\"", "<svg$1");
                svg = svg.replaceFirst("<svg([^>]*)\\sheight=\"[^\"]+\"", "<svg$1");
                scramblerDTO.setSvg(svg);
                scramblerRepository.save(scramblerDTO.toEntity());
            }
        }
    }

    public ScramblerDTO getScramble(Integer compIdx, String uid){
        RecorderResponseDTO record=participateService.getForRecorder(uid, compIdx);
        ParticipateDTO participate=participateService.getParticipateOne(record.getIdx());
        return new ScramblerDTO(scramblerRepository.findByRound_IdxAndRoundGroupAndSeq(participate.getRound().getIdx(), participate.getRoundGroup(), record.getSeq().toString()));
    }

    public Map<String, List<ScramblerDTO>> getScrambleList(Integer roundIdx){
        List<Scrambler> list=scramblerRepository.findByRound_Idx(roundIdx);
        return list.stream()
                .collect(Collectors.groupingBy(
                        Scrambler::getRoundGroup, // roundGroup으로 묶기
                        Collectors.mapping(ScramblerDTO::new, Collectors.toList())
                ));
    }

    public void deleteScramble(Integer roundIdx){
        scramblerRepository.deleteByRound_Idx(roundIdx);
    }
}
