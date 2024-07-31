package JGS.CasperEvent.global.error.exception;

public enum ErrorCode {
    AUTHENTICATION_REQUIRED(401),
    ACCESS_DENIED(403),
    INTERNAL_SERVER_ERROR(500),
    USER_NOT_FOUND(404),
    INVALID_REQUEST_ERROR(400);

    private final int status;

    ErrorCode(int status) {
        this.status = status;
    }

    public int getStatus(){
        return status;
    }
}
