package com.cubingclub.ranking.Exception.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER-001", "유저가 존재하지 않습니다."),

    COMPETITION_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMPETITION-001", "해당 대회가 존재하지 않습니다."),

    ROUND_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROUND-001", "해당 라운드가 존재하지 않습니다."),
    ROUND_GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROUND-002", "해당 라운드의 그룹이 존재하지 않습니다."),

    CUBE_EVENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "CUBEEVENT-001", "해당 종목이 존재하지 않습니다."),
    CUBE_EVENT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CUBEEVENT-002", "큐브 종목 저장이 실패했습니다."),

    SCRAMBLE_SCRAMBLER_NOT_FOUND(HttpStatus.BAD_REQUEST, "SCRAMBLE-001", "스크램블러 정보가 없습니다."),

    RECORD_NOT_FOUND(HttpStatus.BAD_REQUEST, "RECORD-001", "기록 정보가 없습니다."),

    PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, "ACCOUNT-002", "비밀번호가 일치하지 않습니다."),
    ID_DUPLICATE(HttpStatus.CONFLICT, "ACCOUNT-003", "이미 존재하는 아이디입니다."),
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "ACCOUNT-004", "이미 존재하는 이메일입니다."),
    DISABLED_ACCOUNT(HttpStatus.UNAUTHORIZED, "ACCOUNT-005", "비활성화된 계정입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT-006", "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT-007", "유효하지 않은 리프레시 토큰입니다."),
    USERADD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "ACCOUNT-008", "사용자 추가에 실패했습니다."),
    NEW_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "ACCOUNT-009", "비밀번호 확인이 틀립니다."),

    CONFLICT_ACCOUNT_DELETE(HttpStatus.CONFLICT, "ACCOUNT-010", "본인 계정을 삭제하고 있습니다.");

    private final HttpStatus httpStatus;	// HttpStatus
    private final String code;				// ACCOUNT-001
    private final String message;			// 설명
}
