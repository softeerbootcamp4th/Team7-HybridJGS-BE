package JGS.CasperEvent.global.error.exception;

public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
