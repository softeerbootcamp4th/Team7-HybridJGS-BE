package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

import com.google.gson.annotations.SerializedName;

public enum Color {
    @SerializedName("1")
    BUTTERCREAM_YELLOW_PEARL(1),
    @SerializedName("2")
    SIENNA_ORANGE_METALLIC(2),
    @SerializedName("3")
    DUSK_BLUE_MATT(3),
    @SerializedName("4")
    AERO_SILVER_MATT(4),
    @SerializedName("5")
    ABYSS_BLACK_PEARL(5),
    @SerializedName("6")
    LIME_GREEN(6),
    @SerializedName("7")
    TEAL_GREEN(7),
    @SerializedName("8")
    LIGHT_BLUE(8),
    @SerializedName("9")
    SKY_BLUE(9),
    @SerializedName("10")
    INDIGO(10),
    @SerializedName("11")
    DEEP_PURPLE(11),
    @SerializedName("12")
    PURPLE(12),
    @SerializedName("13")
    MAGENTA(13),
    @SerializedName("14")
    RED(14),
    @SerializedName("15")
    ORANGE(15),
    @SerializedName("16")
    GOLD(16),
    @SerializedName("17")
    YELLOW(17);

    private final int color;

    Color(int color)
    {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
