package hunternif.mc.atlas.util;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;

public class ShortVec3 {
	public short x;
	public short y;
	public short z;
	
	public ShortVec3(short x, short y, short z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public ShortVec3(int x, int y, int z) {
		this.x = (short)x;
		this.y = (short)y;
		this.z = (short)z;
	}
	
	public ShortVec3(Vec3 vec3) {
		x = (short)MathHelper.floor_double(vec3.xCoord);
		y = (short)MathHelper.floor_double(vec3.yCoord);
		z = (short)MathHelper.floor_double(vec3.zCoord);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public ShortVec3 copy() {
		return new ShortVec3(x, y, z);
	}
	
	public double distanceTo(ShortVec3 intVec3) {
		double x1 = x;
		double y1 = y;
		double z1 = z;
		double x2 = intVec3.x;
		double y2 = intVec3.y;
		double z2 = intVec3.z;
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ShortVec3))
			return false;
		ShortVec3 vec = (ShortVec3) obj;
		return vec.x == x && vec.y == y && vec.z == z;
	}
	
	@Override
	public int hashCode() {
		return (int)x + (z << 16) + (y << 25);
	}
	
	public boolean equalsIntVec3(ShortVec3 vec) {
		return vec.x == x && vec.y == y && vec.z == z;
	}
	
	public Vec3 toVec3(Vec3Pool pool) {
		return pool.getVecFromPool(x, y, z);
	}
}
