package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

import com.google.gson.annotations.SerializedName;

public enum EyePosition {
    @SerializedName("1")
    CENTER(1),
    @SerializedName("2")
    LEFT(2),
    @SerializedName("3")
    RIGHT(3);

    private final int eyePosition;

    EyePosition(int eyePosition) {
        this.eyePosition = eyePosition;
    }

    public int getEyePosition() {
        return eyePosition;
    }
}
