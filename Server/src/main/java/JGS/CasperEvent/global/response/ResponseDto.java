package JGS.CasperEvent.global.response;

public record ResponseDto(String message) {
    public static ResponseDto of(String message) {
        return new ResponseDto(message);
    }
}

