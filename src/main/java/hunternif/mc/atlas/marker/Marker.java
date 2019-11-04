package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.client.resources.I18n;

/**
 * Marker on the map in an atlas. Has a type and a text label.
 * @author Hunternif
 */
public class Marker {
	
	/**Longest allowed type length*/
	public static final int TYPE_LIMIT = 128;
	/**Longest allowed label length*/
	public static final int LABEL_LIMIT = 128;
	
	/** Id is unique only within a MarkersData instance, i.e. within one atlas
	 * or among global markers in a world. */
	private final int id;
	private final String type;
	private final String label;
	private final int dim, x, z;
	private final boolean visibleAhead;
	private boolean isGlobal;
	
	//TODO make an option for the marker to disappear at a certain scale.
	
	public Marker(int id, String type, String label, int dimension, int x, int z, boolean visibleAhead) {
		this.id = id;
		if (type.length() > TYPE_LIMIT){
			type = type.substring(0, TYPE_LIMIT);
			Log.warn("Marker type is to long. Trimming to %d characters. Type: %s", LABEL_LIMIT, type);
		}
		this.type = type;
		if (label.length() > LABEL_LIMIT){
			label = label.substring(0, LABEL_LIMIT);
			Log.warn("Marker type is to long. Trimming to %d characters. Type: %s", LABEL_LIMIT, label);
		}
		this.label = label == null ? "" : label;
		this.dim = dimension;
		this.x = x;
		this.z = z;
		this.visibleAhead = visibleAhead;
	}
	
	public int getId() {
		return id;
	}

	/**
	 * Note that this is only the string representation of the type, e.g.: "antiqueatlas:village".
	 * To check marker type, better to use {@link #isOfType(MarkerType)}
	 */
	public String getType() {
		return type;
	}

	public boolean isOfType(MarkerType type) {
		return getType().equals(type.getRegistryName().toString());
	}

	/** The label "as is", it might be a placeholder in the format
	 * "gui.antiqueatlas.marker.*" that has to be translated.
	 */
	public String getLabel() {
		return label;
	}
	public String getLocalizedLabel() {
		// Assuming the beginning of the label string until a whitespace (or end)
		// is a translatable key. What comes after it is assumed to be a single
		// string parameter, i.e. player's name.
		String[] parts = label.split("\\s", 2);
		String key = parts[0];
		// formatting only applies when the first word is a valid i18n key
		if (key.matches("([a-zA-Z_]\\.?)+")) {
			String param = parts.length > 1 ? parts[1] : "";
			String translated = I18n.format(key, param);
			if (!key.equals(translated)) { // translation succeeded
				return translated;
			}
		}
		return label;
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
	
	/** Whether the marker is visible regardless of the player having seen the location. */
	public boolean isVisibleAhead() {
		return visibleAhead;
	}
	
	public boolean isGlobal() {
		return isGlobal;
	}
	Marker setGlobal(boolean value) {
		this.isGlobal = value;
		return this;
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
