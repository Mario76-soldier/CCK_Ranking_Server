package com.cubingclub.ranking.Scrambler.Component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PuzzleColorScheme {
    private final Map<String, String> cube = Map.of(
            "B", "#0000ff",
            "R", "#ff0000",
            "D", "#ffff00",
            "U", "#ffffff",
            "F", "#00ff00",
            "L", "#ff8000"
    );

    private final Map<String, String> tetra = Map.of(
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

    private final Map<String, String> skewb = Map.of(
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

    private final Map<String, String> square = Map.of(
            "B", "#ff8000",
            "R", "#00ff00",
            "D", "#ffffff",
            "U", "#ffff00",
            "F", "#ff0000",
            "L", "#0000ff"
    );

    public Map<String, String> getColorScheme(String colorType) {
        return switch (colorType) {
            case "cube" -> cube;
            case "tetra" -> tetra;
            case "dode" -> dode;
            case "skewb" -> skewb;
            case "clock" -> clock;
            case "square" -> square;
            default -> null;
        };
    }
}
