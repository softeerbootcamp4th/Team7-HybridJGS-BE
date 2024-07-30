package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

import com.google.gson.annotations.SerializedName;

public enum EyeShape {
    @SerializedName("1")
    ALLOY_WHEEL_15(1),
    @SerializedName("2")
    ALLOY_WHEEL_17(2),
    @SerializedName("3")
    PIXEL_BY_PIXEL(3),
    @SerializedName("4")
    ELECTRIC(4),
    @SerializedName("5")
    GLASSY(5),
    @SerializedName("6")
    SMILE(6),
    @SerializedName("7")
    CUTE(7),
    @SerializedName("8")
    HEART(8);

    private final int eyeShape;

    EyeShape(int eyeShape) {
        this.eyeShape = eyeShape;
    }

    public int getEyeShape() {
        return eyeShape;
    }
}
