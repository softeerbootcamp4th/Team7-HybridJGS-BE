package JGS.CasperEvent.global.response;

public record CustomResponse<T>(String message, T result) {
    public static <T> CustomResponse<T> response(String message, T result) {
        return new CustomResponse<>(message, result);
    }

    public static <T> CustomResponse<T> success(T result){
        return new CustomResponse<>("요청에 성공했습니다.", result);
    }

    public static <T> CustomResponse<T> create(T result){
        return new CustomResponse<>("생성에 성공했습니다.", result);
    }
}

