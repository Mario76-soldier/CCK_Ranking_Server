package com.cubingclub.ranking.Scrambler.Service;

import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Scrambler.Component.ScrambleGenerator;
import com.cubingclub.ranking.Scrambler.DTO.ScrambleResultDTO;
import com.cubingclub.ranking.Scrambler.Entity.Scramble;
import com.cubingclub.ranking.Scrambler.Repository.ScramblerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrambleCreationService {
    private final ScramblerRepository scramblerRepository;
    private final ScrambleGenerator scrambleGenerator;

    @Transactional
    public void generateNormalScrambles(Round round, String group, int count, String puzzleId, Map<String, String> colorScheme) {
        for (int i = 1; i <= count; i++) {
            ScrambleResultDTO result = (puzzleId == null || colorScheme == null)
                    ? new ScrambleResultDTO(null, null)
                    : scrambleGenerator.generate(puzzleId, colorScheme);

            scramblerRepository.save(
                    createScramble(
                            round,
                            group,
                            String.valueOf(i),
                            result.scramble(),
                            result.svg()
                    )
            );
        }
    }

    @Transactional
    public void generateExtraScrambles(Round round, String group, String puzzleId, Map<String, String> colorScheme) {
        if (puzzleId == null || colorScheme == null) return;

        for (int i = 1; i <= 2; i++) {
            ScrambleResultDTO result = scrambleGenerator.generate(puzzleId, colorScheme);

            scramblerRepository.save(
                    createScramble(
                            round,
                            group,
                            "ex" + i,
                            result.scramble(),
                            result.svg()
                    )
            );
        }
    }

    public boolean scrambleExistsForGroup(Integer roundIdx, String group) {
        return !scramblerRepository
                .findByRound_IdxAndRoundGroup(roundIdx, group)
                .isEmpty();
    }

    private Scramble createScramble(Round round, String group, String seq, String scramble, String svg) {
        return Scramble.builder()
                .round(round)
                .roundGroup(group)
                .seq(seq)
                .scramble(scramble)
                .svg(svg)
                .build();
    }
}
