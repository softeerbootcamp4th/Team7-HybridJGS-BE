package JGS.CasperEvent.global.enums;

import lombok.Getter;

@Getter
public enum CustomErrorCode {
    NO_RUSH_EVENT("선착순 이벤트를 찾을 수 없습니다.", 404),
    INVALID_PARAMETER("잘못된 파라미터 입력입니다.", 400),
    CASPERBOT_NOT_FOUND("배지를 찾을 수 없습니다.", 404),
    BAD_REQUEST("잘못된 요청입니다.", 400),
    UNAUTHORIZED("권한이 없습니다.", 401),
    USER_NOT_FOUND("응모하지 않은 사용자입니다.", 404),
    CONFLICT("이미 존재하는 ID입니다.", 409),
    JWT_PARSE_EXCEPTION("Json 파싱 오류입니다.", 400),
    JWT_EXCEPTION("JWT 오류입니다.", 400),
    JWT_EXPIRED("만료된 토큰입니다.", 400),
    JWT_MISSING("인증 토큰이 존재하지 않습니다.", 401),
    LOTTERYEVENT_ALREADY_EXISTS("이미 추첨 이벤트가 존재합니다.", 409);



    private final String message;
    private int status;

    CustomErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }

    CustomErrorCode(String message) {
        this.message = message;
    }
}
