package JGS.CasperEvent.global.jwt.dto;

import lombok.Getter;

@Getter
public class AdminLoginDto implements LoginDto {
    private String id;
    private String password;
}
