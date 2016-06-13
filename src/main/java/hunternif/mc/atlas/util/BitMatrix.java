package hunternif.mc.atlas.util;

import java.util.BitSet;

public class BitMatrix {

	private BitSet set;
	private int width, height;
	
	public BitMatrix(int width, int height) {
		this.width = width;
		this.height = height;
		set = new BitSet(width*height);
	}
	
	public BitMatrix(int width, int height, boolean initialValue) {
		this(width, height);
		
		set.set(0, set.size()-1, initialValue);
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void set(int x, int y, boolean value) {
		if(x < 0 || y < 0 || x >= width || y >= height)
			return;
		set.set(calcIndex(x,y), value);
	}
	
	public boolean get(int x, int y) {
		if(x < 0 || y < 0 || x >= width || y >= height)
			return false;
		return set.get(calcIndex(x,y));
	}
	
	private int calcIndex(int x, int y) {
		return x + y*width;
	}
}
