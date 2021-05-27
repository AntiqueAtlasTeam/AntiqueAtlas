package hunternif.mc.impl.atlas.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import hunternif.mc.impl.atlas.forge.NbtType;
import hunternif.mc.impl.atlas.network.packet.s2c.play.CustomTileInfoS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

/**
 * This world-saved data contains all the non-biome tiles in a world.
 * Atlases check with it when updating themselves.
 * @author Hunternif
 */
public class TileDataStorage extends WorldSavedData {
	private static final int VERSION = 3;
	private static final String TAG_VERSION = "aaVersion";
	private static final String TAG_TILE_LIST = "tiles";

	public TileDataStorage(String key) {
		super(key);
	}

	private final Map<ChunkPos, ResourceLocation> tiles = new ConcurrentHashMap<>(2, 0.75f, 2);

	@Override
	public void read(CompoundNBT compound) {
		int version = compound.getInt(TAG_VERSION);

		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}

		ListNBT tileList = compound.getList(TAG_TILE_LIST, NbtType.CompoundNBT);

		tileList.forEach(tag1 -> {
			CompoundNBT tile = (CompoundNBT) tag1;
			ChunkPos coords = new ChunkPos(tile.getInt("x"), tile.getInt("y"));
			tiles.put(coords, ResourceLocation.tryCreate(tile.getString("id")));
		});
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt(TAG_VERSION, VERSION);

		ListNBT tileList = new ListNBT();

		for (Entry<ChunkPos, ResourceLocation> entry : tiles.entrySet()) {
			CompoundNBT tile = new CompoundNBT();
			tile.putInt("x", entry.getKey().x);
			tile.putInt("y", entry.getKey().z);
			tile.putString("id", entry.getValue().toString());

			tileList.add(tile);
		}

		compound.put(TAG_TILE_LIST, tileList);

		return compound;
	}
	
	/** If no custom tile is set at the specified coordinates, returns null. */
	public ResourceLocation getTile(int x, int z) {
		return tiles.get(new ChunkPos(x,z));
	}
	
	/** If setting tile on the server, a packet should be sent to all players. */
	public void setTile(int x, int z, ResourceLocation tile) {
		tiles.put(new ChunkPos(x, z), tile);
		markDirty();
	}
	
	public void removeTile(int x, int z) {
		tiles.remove(new ChunkPos(x, z));
		markDirty();
	}

	/** Send all data to player in several zipped packets. */
	public void syncToPlayer(ServerPlayerEntity player, RegistryKey<World> world) {
		new CustomTileInfoS2CPacket(world, tiles).send(player);

		Log.info("Sent custom biome data to player %s", player.getCommandSource().getName());
	}

}
