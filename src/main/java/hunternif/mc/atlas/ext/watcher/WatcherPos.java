package hunternif.mc.atlas.ext.watcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.BlockPos;

public class WatcherPos {

    public static final Pattern POS_PATTERN = Pattern.compile("\\[([-\\d]+),([-\\d]+)\\]");

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
        Matcher matcher = POS_PATTERN.matcher(coords);
        if (!matcher.matches())
            throw new IllegalArgumentException("Improper coordinate format provided: " + coords);

        this.x = Integer.parseInt(matcher.group(1));
        this.z = Integer.parseInt(matcher.group(2));
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
