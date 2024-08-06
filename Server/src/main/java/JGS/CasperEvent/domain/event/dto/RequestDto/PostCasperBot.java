package JGS.CasperEvent.domain.event.dto.RequestDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PostCasperBot {

    @NotNull(message = "눈 모양 값은 필수 필드입니다.")
    @Min(value = 0, message = "눈 모양 값이 부적절합니다.")
    @Max(value = 7, message = "눈 모양 값이 부적절합니다.")
    private Integer eyeShape;

    @NotNull(message = "눈 위치 값은 필수 필드입니다.")
    @Min(value = 0, message = "눈 위치 값이 부적절합니다.")
    @Max(value = 2, message = "눈 위치 값이 부적절합니다.")
    private Integer eyePosition;

    @NotNull(message = "입 모양 값은 필수 필드입니다.")
    @Min(value = 0, message = "입 모양 값이 부적절합니다.")
    @Max(value = 4, message = "입 모양 값이 부적절합니다.")
    private Integer mouthShape;

    @NotNull(message = "색깔 값은 필수 필드입니다.")
    @Min(value = 0, message = "색깔 값이 부적절합니다.")
    @Max(value = 17, message = "색깔 값이 부적절합니다.")
    private Integer color;

    @NotNull(message = "스티커 값은 필수 필드입니다.")
    @Min(value = 0, message = "스티커 값이 부적절합니다.")
    @Max(value = 4, message = "스티커 값이 부적절합니다.")
    private Integer sticker;

    @NotNull(message = "이름은 필수 필드입니다. ")
    private String name;

    private String expectation;


    public Integer getEyeShape() {
        return eyeShape;
    }

    public Integer getEyePosition() {
        return eyePosition;
    }

    public Integer getMouthShape() {
        return mouthShape;
    }

    public Integer getColor() {
        return color;
    }

    public Integer getSticker() {
        return sticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpectation() {
        return expectation;
    }
}
