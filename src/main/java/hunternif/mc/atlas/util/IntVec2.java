package hunternif.mc.atlas.util;

import net.minecraft.util.math.MathHelper;

public class IntVec2 {
	public int x;
	public int y;
	
	public IntVec2(IntVec2 vec) {
		this(vec.x, vec.y);
	}
	
	private IntVec2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public IntVec2(double x, double y) {
		this.x = MathHelper.floor(x);
		this.y = MathHelper.floor(y);
	}
	
	/** Modifies and returns self. */
	public IntVec2 add(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}
	
	/** Modifies and returns self. */
	public IntVec2 set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	public IntVec2 clone() {
		return new IntVec2(x, y);
	}
	
	public double distanceTo(IntVec2 intVec2) {
		double x1 = x;
		double y1 = y;
		double x2 = intVec2.x;
		double y2 = intVec2.y;
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntVec2))
			return false;
		IntVec2 vec = (IntVec2) obj;
		return vec.x == x && vec.y == y;
	}
	
	@Override
	public int hashCode() {
		return x + (y << 16);
	}
	
	public boolean equalsIntVec3(IntVec2 vec) {
		return vec.x == x && vec.y == y;
	}
}
