package JGS.CasperEvent.domain.event.dto.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AdminRequestDto {

    @NotNull
    private String adminId;

    @NotNull
    private String password;

}
