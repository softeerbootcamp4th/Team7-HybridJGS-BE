package JGS.CasperEvent.global.error;

import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handler(CustomException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CustomErrorCode.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> missingCookieHandler(){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(CustomErrorCode.UNAUTHORIZED));
    }

    @ExceptionHandler(UserPrincipalNotFoundException.class)
    public ResponseEntity<ErrorResponse> userPrincipalNotFoundHandler(){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(CustomErrorCode.USER_NOT_FOUND));
    }
}
