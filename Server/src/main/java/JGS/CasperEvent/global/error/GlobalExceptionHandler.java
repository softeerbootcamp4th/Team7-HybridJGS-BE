package JGS.CasperEvent.global.error;

import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.response.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static JGS.CasperEvent.global.response.CustomErrorResponse.returnError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse> handler(CustomException e){
        return new ResponseEntity<>(returnError(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<CustomErrorResponse> missingCookieHandler(MissingRequestCookieException e){
        return new ResponseEntity<>(returnError("유저 정보가 없습니다."), HttpStatus.UNAUTHORIZED);
    }
}
