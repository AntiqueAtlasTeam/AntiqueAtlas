package hunternif.mc.atlas.util;

import net.minecraft.util.math.MathHelper;

public class ShortVec2 {
	public short x;
	public short y;
	
	public ShortVec2(ShortVec2 vec) {
		this(vec.x, vec.y);
	}
	
	public ShortVec2(short x, short y) {
		this.x = x;
		this.y = y;
	}
	
	public ShortVec2(int x, int y) {
		this.x = (short)x;
		this.y = (short)y;
	}
	
	public ShortVec2(double x, double y) {
		this.x = (short)MathHelper.floor(x);
		this.y = (short)MathHelper.floor(y);
	}
	
	/** Modifies and returns self. */
	public ShortVec2 add(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}
	
	/** Modifies and returns self. */
	public ShortVec2 set(int x, int y) {
		this.x = (short)x;
		this.y = (short)y;
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	public ShortVec2 clone() {
		return new ShortVec2(x, y);
	}
	
	public double distanceTo(ShortVec2 intVec2) {
		double x1 = x;
		double y1 = y;
		double x2 = intVec2.x;
		double y2 = intVec2.y;
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ShortVec2))
			return false;
		ShortVec2 vec = (ShortVec2) obj;
		return vec.x == x && vec.y == y;
	}
	
	@Override
	public int hashCode() {
		return (int)x + (y << 16);
	}
	
	public boolean equalsIntVec3(ShortVec2 vec) {
		return vec.x == x && vec.y == y;
	}
}
