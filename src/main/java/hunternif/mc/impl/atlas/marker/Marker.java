package hunternif.mc.impl.atlas.marker;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Marker on the map in an atlas. Has a type and a text label.
 * @author Hunternif
 */
public class Marker {
	/** Id is unique only within a MarkersData instance, i.e. within one atlas
	 * or among global markers in a world. */
	private final int id;
	private final ResourceLocation type;
	private final Component label;
	private final ResourceKey<Level> world;
	private final int x, z;
	private final boolean visibleAhead;
	private boolean isGlobal;

	//TODO make an option for the marker to disappear at a certain scale.

	public Marker(int id, ResourceLocation type, Component label, ResourceKey<Level> world, int x, int z, boolean visibleAhead) {
		this.id = id;
		this.type = type;

		this.label = label;
		this.world = world;
		this.x = x;
		this.z = z;
		this.visibleAhead = visibleAhead;
	}

	public Marker(ResourceLocation type, ResourceKey<Level> world, Precursor precursor) {
		this(precursor.id, type, precursor.label, world, precursor.x, precursor.z, precursor.visibleAhead);
	}

	public int getId() {
		return id;
	}

	public ResourceLocation getType() {
		return type;
	}

	/** The label "as is", it might be a placeholder in the format
	 * "gui.antiqueatlas.marker.*" that has to be translated.
	 */
	public Component getLabel() {
		return label;
	}

	public ResourceKey<Level> getWorld() {
		return this.world;
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
	public ChunkPos getChunkCoords() {
		return new ChunkPos(new BlockPos(x, 0, z));
	}

	@Override
	public String toString() {
		return "#" + id + "\"" + label.getString() + "\"" + "@(" + x + ", " + z + ")";
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeComponent(this.label);
		buf.writeVarInt(this.x);
		buf.writeVarInt(this.z);
		buf.writeBoolean(this.visibleAhead);
	}

	public static class Precursor {
		private final int id;
		private final Component label;
		private final int x, z;
		private final boolean visibleAhead;


		public Precursor(FriendlyByteBuf buf) {
			this.id = buf.readVarInt();
			this.label = buf.readComponent();
			this.x = buf.readVarInt();
			this.z = buf.readVarInt();
			this.visibleAhead = buf.readBoolean();
		}

		public Precursor(Marker marker) {
			this.id = marker.id;
			this.label = marker.label;
			this.x = marker.x;
			this.z = marker.z;
			this.visibleAhead = marker.visibleAhead;
		}
	}
}
