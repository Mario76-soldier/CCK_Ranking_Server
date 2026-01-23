package com.cubingclub.ranking.Scrambler.Service;

import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Record.DTO.RecorderResponseDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Record.Service.RecordService;
import com.cubingclub.ranking.Record.Service.RecorderService;
import com.cubingclub.ranking.Scrambler.DTO.ScrambleResponseDTO;
import com.cubingclub.ranking.Scrambler.Entity.Scramble;
import com.cubingclub.ranking.Scrambler.Repository.ScramblerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrambleQueryService {
    private final AttemptRepository attemptRepository;
    private final ScramblerRepository scramblerRepository;
    private final AuthService authService;
    private final RecordService recordService;
    private final RecorderService recorderService;

    @Transactional
    public ScrambleResponseDTO getScrambleForPlayer(Integer compIdx, String playerUid, String scramblerUid, String token) {
        PlayerInfo playerInfo = authenticatePlayer(playerUid, token);
        PlayerInfo scramblerInfo = authenticatePlayer(scramblerUid, token);

        AttemptContext attemptContext = getAttemptContext(playerInfo.getCckId(), compIdx);

        Scramble scramble = fetchScramble(attemptContext);

        ScrambleResponseDTO response = buildResponse(scramble, playerInfo, attemptContext.attempt.getIdx());
        updateScramblerInfo(attemptContext.attempt.getIdx(), scramblerInfo);
        
        return response;
    }

    private PlayerInfo authenticatePlayer(String uid, String token) {
        return authService.getRFIDInfo(token, uid)
                .orElseThrow(() -> new CustomizedException(ErrorCode.USER_NOT_FOUND));
    }

    private AttemptContext getAttemptContext(String cckId, Integer compIdx) {
        RecorderResponseDTO record = recorderService.getForRecorder(cckId, compIdx);
        Attempt attempt = attemptRepository.findByIdx(record.getAttemptIdx());
        RecordDTO recordDTO = new RecordDTO(attempt.getRecord());
        
        validateRoundGroup(recordDTO);
        
        String sequence = resolveSequence(record);
        return new AttemptContext(record, attempt, recordDTO, sequence);
    }

    private void validateRoundGroup(RecordDTO recordDTO) {
        if (recordDTO.getRoundGroup() == null) {
            throw new CustomizedException(ErrorCode.ROUND_GROUP_NOT_FOUND);
        }
    }

    private String resolveSequence(RecorderResponseDTO recorderResponseDTO) {
        if (recorderResponseDTO.getExtraNum() != null) {
            return "ex" + recorderResponseDTO.getExtraNum();
        }
        return recorderResponseDTO.getSeq().toString();
    }

    private Scramble fetchScramble(AttemptContext attemptContext) {
        return scramblerRepository.findByRound_IdxAndRoundGroupAndSeq(
                attemptContext.recordDTO.getRoundIdx(),
                attemptContext.recordDTO.getRoundGroup(),
                attemptContext.sequence
        );
    }

    private ScrambleResponseDTO buildResponse(Scramble scramble, PlayerInfo playerInfo, Integer attemptIdx) {
        ScrambleResponseDTO response = new ScrambleResponseDTO(scramble);
        response.setName(playerInfo.getName());
        response.setCckId(playerInfo.getCckId());
        response.setAttemptIdx(attemptIdx);
        return response;
    }

    private void updateScramblerInfo(Integer attemptIdx, PlayerInfo scramblerInfo) {
        recordService.updateScrambler(attemptIdx, scramblerInfo);
    }

    private record AttemptContext(RecorderResponseDTO record, Attempt attempt, RecordDTO recordDTO, String sequence) {}
}
