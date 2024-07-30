package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

import com.google.gson.annotations.SerializedName;

public enum Sticker {
    @SerializedName("1")
    ELECTRIC_SHOCK(1),
    @SerializedName("2")
    FULL_CHARGE(2),
    @SerializedName("3")
    BATTERY_BLINKING(3),
    @SerializedName("4")
    LOVELY_RIBBON(4),
    @SerializedName("5")
    SPARKLING(5);

    private final int sticker;

    Sticker(int sticker) {
        this.sticker = sticker;
    }

    public int getSticker() {
        return sticker;
    }
}
