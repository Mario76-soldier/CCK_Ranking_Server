package com.cubingclub.ranking.Scrambler.Service;

import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Scrambler.Component.PuzzleConfigResolver;
import com.cubingclub.ranking.Scrambler.DTO.ScrambleResponseDTO;
import com.cubingclub.ranking.Scrambler.DTO.ScramblerUpdateDTO;
import com.cubingclub.ranking.Scrambler.Repository.ScramblerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScramblerService {
    private final RoundRepository roundRepository;
    private final ScramblerRepository scramblerRepository;
    private final PuzzleConfigResolver puzzleConfigResolver;
    private final ScrambleCreationService scrambleCreationService;
    private final ScrambleQueryService scrambleQueryService;

    @Transactional
    public void saveScramble(Integer roundIdx, List<String> groups) {
        scramblerRepository.deleteByRound_Idx(roundIdx);

        Round round = roundRepository.findByIdx(roundIdx).orElseThrow(() -> new CustomizedException(ErrorCode.ROUND_NOT_FOUND));
        // OneToMany 컬렉션 초기화
        Hibernate.initialize(round.getRecords());
        Hibernate.initialize(round.getScrambles());
        
        String puzzleId = puzzleConfigResolver.resolvePuzzleId(round);
        int count = puzzleConfigResolver.resolveScrambleCount(round);
        Map<String, String> colorScheme = puzzleConfigResolver.resolveColorScheme(round);

        for (String group : groups) {
            if (scrambleCreationService.scrambleExistsForGroup(roundIdx, group)) {
                continue;
            }

            scrambleCreationService.generateNormalScrambles(round, group, count, puzzleId, colorScheme);
            scrambleCreationService.generateExtraScrambles(round, group, puzzleId, colorScheme);
        }
    }

    @Transactional
    public ScrambleResponseDTO getScramble(ScramblerUpdateDTO scramblerUpdateDTO, String token) {
        return scrambleQueryService.getScrambleForPlayer(scramblerUpdateDTO.getCompIdx()
                , scramblerUpdateDTO.getPlayerUid()
                , scramblerUpdateDTO.getScramblerUid()
                , token);
    }
}
