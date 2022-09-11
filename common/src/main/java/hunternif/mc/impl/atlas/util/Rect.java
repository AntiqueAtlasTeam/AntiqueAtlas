package hunternif.mc.impl.atlas.util;

public class Rect {
    public int minX, minY, maxX, maxY;

    public Rect() {
        this(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public Rect(int minX, int minY, int maxX, int maxY) {
        this.set(minX, minY, maxX, maxY);
    }

    public Rect(Rect r) {
        this(r.minX, r.minY, r.maxX, r.maxY);
    }

    public Rect set(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return this;
    }

    public Rect set(Rect r) {
        this.set(r.minX, r.minY, r.maxX, r.maxY);
        return this;
    }

    /**
     * Set minX and minY.
     */
    public Rect setOrigin(int x, int y) {
        this.minX = x;
        this.minY = y;
        return this;
    }

    /**
     * Set maxX and maxY, assuming that minX and minY are already set.
     */
    public Rect setSize(int width, int height) {
        this.maxX = this.minX + width;
        this.maxY = this.minY + height;
        return this;
    }

    public int getWidth() {
        return maxX - minX;
    }

    public int getHeight() {
        return maxY - minY;
    }

    /**
     * Extend the bounds to include the given point.
     */
    public void extendTo(int x, int y) {
        if (x < minX) minX = x;
        if (x > maxX) maxX = x;
        if (y < minY) minY = y;
        if (y > maxY) maxY = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Rect)) return false;
        Rect r = (Rect) obj;
        return minX == r.minX && minY == r.minY && maxX == r.maxX && maxY == r.maxY;
    }

    @Override
    public String toString() {
        return String.format("Rect{%d, %d, %d, %d}", minX, minY, maxX, maxY);
    }
}
