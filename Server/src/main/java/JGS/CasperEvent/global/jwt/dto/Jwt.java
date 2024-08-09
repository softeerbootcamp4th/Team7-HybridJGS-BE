package JGS.CasperEvent.global.jwt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Jwt {
    private String accessToken;

    @Builder
    public Jwt(String accessToken){
        this.accessToken = accessToken;
    }
}