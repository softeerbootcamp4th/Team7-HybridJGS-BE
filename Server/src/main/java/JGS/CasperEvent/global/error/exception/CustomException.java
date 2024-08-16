package JGS.CasperEvent.global.error.exception;

import JGS.CasperEvent.global.enums.CustomErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomErrorCode errorCode;

    public CustomException(String message, CustomErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomException(CustomErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
