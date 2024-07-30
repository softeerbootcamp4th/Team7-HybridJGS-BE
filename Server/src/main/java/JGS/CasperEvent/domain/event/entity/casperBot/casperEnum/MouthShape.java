package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

import com.google.gson.annotations.SerializedName;

public enum MouthShape {
    @SerializedName("1")
    SMILING(1),
    @SerializedName("2")
    DISPLEASED(2),
    @SerializedName("3")
    GRINNING(3),
    @SerializedName("4")
    BEAMING(4),
    @SerializedName("5")
    NEUTRAL(5);

    private final int mouthShape;

    MouthShape(int mouthShape)
    {
        this.mouthShape = mouthShape;
    }

    public int getMouthShape() {
        return mouthShape;
    }
}
