package com.cubingclub.ranking.Scrambler.Component;

import com.cubingclub.ranking.Round.Entity.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PuzzleConfigResolver {
    private final PuzzleColorScheme colorScheme;

    public String resolvePuzzleId(Round round) {
        String eventName = round.getCubeEvent().getEventName();

        String baseEvent;
        String option = "";

        if (eventName.contains(" ")) {
            String[] parts = eventName.split(" ", 2);
            baseEvent = parts[0];
            option = parts[1];
        } else {
            baseEvent = eventName;
        }

        String puzzleId = getPuzzleId(baseEvent);
        if (puzzleId == null) return null;

        if (option.startsWith("BLD")) puzzleId += "bf";
        else if (option.startsWith("FMC")) puzzleId += "fm";
        else if (option.startsWith("OH")) puzzleId += "oh";
        else if (option.startsWith("MULTBLD")) puzzleId += "mbf";

        return puzzleId;
    }

    private static String getPuzzleId(String baseEvent) {
        Map<String, String> baseMap = Map.ofEntries(
                Map.entry("2x2x2", "222"),
                Map.entry("3x3x3", "333"),
                Map.entry("4x4x4", "444"),
                Map.entry("5x5x5", "555"),
                Map.entry("6x6x6", "666"),
                Map.entry("7x7x7", "777"),
                Map.entry("Clock", "clock"),
                Map.entry("Megaminx", "minx"),
                Map.entry("Pyraminx", "pyram"),
                Map.entry("Skewb", "skewb"),
                Map.entry("Square-1", "sq1")
        );

        return baseMap.get(baseEvent);
    }

    public int resolveScrambleCount(Round round) {
        String avgCalc = round.getCubeEvent().getAvgCalc();

        if ("single".equals(avgCalc)) {
            return 1;
        }

        // 문자열 끝의 숫자 추출
        String numberPart = avgCalc.replaceAll("\\D+", "");
        return numberPart.isEmpty() ? 0 : Integer.parseInt(numberPart);
    }

    public Map<String, String> resolveColorScheme(Round round) {
        return colorScheme.getColorScheme(String.valueOf(round.getCubeEvent().getColorType()));
    }
}
