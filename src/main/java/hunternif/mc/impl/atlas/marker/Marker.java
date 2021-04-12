package hunternif.mc.impl.atlas.marker;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Marker on the map in an atlas. Has a type and a text label.
 * @author Hunternif
 */
public class Marker {
	/** Id is unique only within a MarkersData instance, i.e. within one atlas
	 * or among global markers in a world. */
	private final int id;
	private final Identifier type;
	private final Text label;
	private final RegistryKey<World> world;
	private final int x, z;
	private final boolean visibleAhead;
	private boolean isGlobal;

	//TODO make an option for the marker to disappear at a certain scale.

	public Marker(int id, Identifier type, Text label, RegistryKey<World> world, int x, int z, boolean visibleAhead) {
		this.id = id;
		this.type = type;

		this.label = label;
		this.world = world;
		this.x = x;
		this.z = z;
		this.visibleAhead = visibleAhead;
	}

	public Marker(Identifier type, RegistryKey<World> world, Precursor precursor) {
		this(precursor.id, type, precursor.label, world, precursor.x, precursor.z, precursor.visibleAhead);
	}

	public int getId() {
		return id;
	}

	public Identifier getType() {
		return type;
	}

	/** The label "as is", it might be a placeholder in the format
	 * "gui.antiqueatlas.marker.*" that has to be translated.
	 */
	public Text getLabel() {
		return label;
	}

	public RegistryKey<World> getWorld() {
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

	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeText(this.label);
		buf.writeVarInt(this.x);
		buf.writeVarInt(this.z);
		buf.writeBoolean(this.visibleAhead);
	}

	public static class Precursor {
		private final int id;
		private final Text label;
		private final int x, z;
		private final boolean visibleAhead;


		public Precursor(PacketByteBuf buf) {
			this.id = buf.readVarInt();
			this.label = buf.readText();
			this.x = buf.readVarInt();
			this.z = buf.readVarInt();
			this.visibleAhead = buf.readBoolean();
		}
	}
}
