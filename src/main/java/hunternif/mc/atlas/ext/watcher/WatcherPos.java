package hunternif.mc.atlas.ext.watcher;

import net.minecraft.util.math.BlockPos;

public class WatcherPos {

    private final int x;
    private final int z;

    public WatcherPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public WatcherPos(BlockPos pos) {
        this(pos.getX(), pos.getY());
    }

    // format = [x, y]
    public WatcherPos(String coords) {
        String[] coordSplit = coords.substring(1, coords.length() - 1).split(",");
        if (coordSplit.length != 2)
            throw new IllegalArgumentException("Improper coordinate format provided: " + coords);

        this.x = Integer.parseInt(coordSplit[0].trim());
        this.z = Integer.parseInt(coordSplit[1].trim());
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WatcherPos)) return false;

        WatcherPos that = (WatcherPos) o;

        if (x != that.x) return false;
        return z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        return result;
    }
}
