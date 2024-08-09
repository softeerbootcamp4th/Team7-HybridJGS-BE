package JGS.CasperEvent.global.error;

import JGS.CasperEvent.global.enums.CustomErrorCode;

public record ErrorResponse(CustomErrorCode errorCode, String message) {
    public static ErrorResponse of(CustomErrorCode e) {
        return new ErrorResponse(
                e, e.getMessage()
        );
    }

    public static ErrorResponse of(CustomErrorCode e, String message){
        return new ErrorResponse(
                e, message
        );
    }

}
