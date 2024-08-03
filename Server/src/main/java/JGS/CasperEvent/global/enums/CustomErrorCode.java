package JGS.CasperEvent.global.enums;

public enum CustomErrorCode {
    NO_RUSH_EVENT("선착순 이벤트를 찾을 수 없습니다."),
    INVALID_CASPERBOT_PARAMETER("잘못된 파라미터 입력입니다."),
    CASPERBOT_NOT_FOUND("배지를 찾을 수 없습니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    UNAUTHORIZED("권한이 없습니다."),
    USER_NOT_FOUND("응모하지 않은 사용자입니다.");

    private final String message;

    CustomErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
