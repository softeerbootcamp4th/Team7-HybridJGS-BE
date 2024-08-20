package JGS.CasperEvent.domain.event.dto.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
public class AdminRequestDto {

    @NotNull
    private String adminId;

    @NotNull
    private String password;

}
