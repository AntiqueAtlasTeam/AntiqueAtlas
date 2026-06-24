package hunternif.mc.impl.atlas.core.scanning;

/**
 * The enum represents the different height levels in biomes.
 */
public enum TileHeightType {
    VALLEY("valley"),
    LOW("low"),
    MID("mid"),
    HIGH("high"),
    PEAK("peak");

    private final String name;

    TileHeightType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }
}
