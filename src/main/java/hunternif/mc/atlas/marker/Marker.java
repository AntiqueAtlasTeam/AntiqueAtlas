package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.util.ShortVec2;

/**
 * Marker on the map in an atlas. Has a type and a text label. Naturally ordered
 * by the y coordinate, so that markers placed closer to the south will appear
 * in front of those placed closer to the north.
 * @author Hunternif
 */
public class Marker implements Comparable<Marker> {
	private final String type;
	private final String label;
	private final int x, y;
	//TODO: Marker should know its dimension
	
	public Marker(String type, String label, int x, int y) {
		this.type = type;
		this.label = label == null ? "" : label;
		this.x = x;
		this.y = y;
	}

	public String getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	/** X coordinate within the chunk. */
	public int getInChunkX() {
		return x & 0xf;
	}
	
	/** Y coordinate within the chunk. */
	public int getInChunkY() {
		return y & 0xf;
	}

	@Override
	public int compareTo(Marker marker) {
		return this.y - marker.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Marker)) return false;
		Marker marker = (Marker) obj;
		return type.equals(marker.type) && label.equals(marker.label) && x == marker.x && y == marker.y;
	}
	
	/** Returns the coordinates of the chunk this marker is located in. */
	public ShortVec2 getChunkCoords() {
		return new ShortVec2(x >> 4, y >> 4);
	}
	
	@Override
	public String toString() {
		return "\"" + label + "\"" + "@(" + x + ", " + y + ")";
	}
}
