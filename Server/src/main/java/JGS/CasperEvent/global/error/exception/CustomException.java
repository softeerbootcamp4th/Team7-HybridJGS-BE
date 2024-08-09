package JGS.CasperEvent.global.error.exception;

import JGS.CasperEvent.global.enums.CustomErrorCode;

public class CustomException extends RuntimeException {
    private final CustomErrorCode errorCode;

    public CustomException(String message, CustomErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomErrorCode getErrorCode() {
        return errorCode;
    }
}
