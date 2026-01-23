package com.cubingclub.ranking.Scrambler.Component;

import com.cubingclub.ranking.Scrambler.DTO.ScrambleResultDTO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ScrambleGenerator {
    private final WebClient webClient;

    public ScrambleResultDTO generate(String puzzleId, Map<String, String> body) {
        JsonNode response = webClient.post()
                .uri("/frontend/puzzle/" + puzzleId + "/scramble")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String scramble = response.get("scramble").asText();
        String svg = response.get("svgImage").asText();

        svg = svg.replaceFirst("<svg([^>]*)\\swidth=\"[^\"]+\"", "<svg$1");
        svg = svg.replaceFirst("<svg([^>]*)\\sheight=\"[^\"]+\"", "<svg$1");

        return new ScrambleResultDTO(scramble, svg);
    }
}
