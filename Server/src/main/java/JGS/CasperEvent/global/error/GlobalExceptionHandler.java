package JGS.CasperEvent.global.error;

import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.response.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import static JGS.CasperEvent.global.response.CustomErrorResponse.returnError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handler(CustomException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<String> missingCookieHandler(){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("유저 정보가 없습니다.");
    }

    @ExceptionHandler(UserPrincipalNotFoundException.class)
    public ResponseEntity<String> userPrincipalNotFoundHandler(){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("응모 내역이 없는 사용자입니다.");
    }
}
