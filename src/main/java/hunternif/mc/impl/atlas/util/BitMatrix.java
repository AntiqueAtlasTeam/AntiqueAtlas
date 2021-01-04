package hunternif.mc.impl.atlas.util;

import java.util.BitSet;

public class BitMatrix {

    private final BitSet set;
    private final int width;
    private final int height;

    private BitMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        set = new BitSet(width * height);
    }

    public BitMatrix(int width, int height, boolean initialValue) {
        this(width, height);

        set.set(0, set.size() - 1, initialValue);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void set(int x, int y, boolean value) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return;
        set.set(calcIndex(x, y), value);
    }

    public boolean get(int x, int y) {
        return !(x < 0 || y < 0 || x >= width || y >= height) && set.get(calcIndex(x, y));
    }

    private int calcIndex(int x, int y) {
        return x + y * width;
    }
}
