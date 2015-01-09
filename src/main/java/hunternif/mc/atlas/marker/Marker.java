package hunternif.mc.atlas.marker;

import net.minecraft.util.StatCollector;
import hunternif.mc.atlas.util.ShortVec2;

/**
 * Marker on the map in an atlas. Has a type and a text label. Naturally ordered
 * by the y coordinate, so that markers placed closer to the south will appear
 * in front of those placed closer to the north.
 * @author Hunternif
 */
public class Marker implements Comparable<Marker> {
	/** Unique id per every global marker and every local marker in every atlas. */
	private final int id;
	private final String type;
	private final String label;
	private final int dim, x, z;
	private final boolean visibleAhead;
	private boolean isGlobal = false;
	
	public Marker(int id, String type, String label, int dimension, int x, int z, boolean visibleAhead) {
		this.id = id;
		this.type = type;
		this.label = label == null ? "" : label;
		this.dim = dimension;
		this.x = x;
		this.z = z;
		this.visibleAhead = visibleAhead;
	}
	
	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	/** The label "as is", it might be a placeholder in the format
	 * "gui.antiqueatlas.marker.*" that has to be translated.
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	public String getLocalizedLabel() {
		return StatCollector.translateToLocal(label);
	}
	
	public int getDimension() {
		return dim;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	/** X coordinate of the chunk. */
	public int getChunkX() {
		return x >> 4;
	}
	
	/** Z coordinate of the chunk. */
	public int getChunkZ() {
		return z >> 4;
	}
	
	/** X coordinate within the chunk. */
	public int getInChunkX() {
		return x & 0xf;
	}
	
	/** Z coordinate within the chunk. */
	public int getInChunkZ() {
		return z & 0xf;
	}
	
	/** Whether the marker is visible regardless of the player having seen the location. */
	public boolean isVisibleAhead() {
		return visibleAhead;
	}
	
	public boolean isGlobal() {
		return isGlobal;
	}
	protected Marker setGlobal(boolean value) {
		this.isGlobal = value;
		return this;
	}

	@Override
	public int compareTo(Marker marker) {
		return this.z - marker.z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Marker)) return false;
		Marker marker = (Marker) obj;
		return this.id == marker.id;
	}
	
	/** Returns the coordinates of the chunk this marker is located in. */
	public ShortVec2 getChunkCoords() {
		return new ShortVec2(x >> 4, z >> 4);
	}
	
	@Override
	public String toString() {
		return "#" + id + "\"" + label + "\"" + "@(" + x + ", " + z + ")";
	}
}
