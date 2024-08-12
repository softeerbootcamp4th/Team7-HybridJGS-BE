package JGS.CasperEvent.global.jwt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginDto {
    String phoneNumber;

    public UserLoginDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
