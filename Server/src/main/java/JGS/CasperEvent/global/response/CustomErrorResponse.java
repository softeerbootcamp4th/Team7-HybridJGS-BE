package JGS.CasperEvent.global.response;

import JGS.CasperEvent.global.error.exception.CustomException;

public record CustomErrorResponse(String message) {
    public static CustomErrorResponse returnError(CustomException e){
        return new CustomErrorResponse(e.getMessage());
    }

    public static CustomErrorResponse returnError(String message){
        return new CustomErrorResponse(message);
    }
}