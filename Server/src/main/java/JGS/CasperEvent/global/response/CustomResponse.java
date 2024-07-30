package JGS.CasperEvent.global.response;

public record CustomResponse<T> (int statusCode, String message, T result){
    public static <T> CustomResponse<T> success(T result){
        return new CustomResponse<>(200, "요청에 성공하였습니다.", result);
    }

    public static <T> CustomResponse<T> create(T result){
        return new CustomResponse<>(201, "생성에 성공하였습니다", result);
    }
}

