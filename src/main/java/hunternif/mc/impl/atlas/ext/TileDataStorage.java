package hunternif.mc.impl.atlas.ext;

import hunternif.mc.impl.atlas.network.packet.s2c.play.CustomTileInfoS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.ShortVec2;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This world-saved data contains all the non-biome tiles in a world.
 * Atlases check with it when updating themselves.
 * @author Hunternif
 */
public class TileDataStorage extends PersistentState {
	private static final int VERSION = 2;
	private static final String TAG_VERSION = "aaVersion";
	private static final String TAG_WORLD_MAP_LIST = "worldMap";
	private static final String TAG_WORLD_ID = "worldID";

	public TileDataStorage(String key) {
		super(key);
	}
	
	private final Map<RegistryKey<World>, Map<ShortVec2, Identifier>> worldMap =
            new ConcurrentHashMap<>(2, 0.75f, 2);
	
	private final ShortVec2 tempCoords = new ShortVec2(0, 0);

	@Override
	public void fromTag(CompoundTag compound) {
		int version = compound.getInt(TAG_VERSION);

		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}

		ListTag worldMapList = compound.getList(TAG_WORLD_MAP_LIST, NbtType.COMPOUND);

		for (int d = 0; d < worldMapList.size(); d++) {
			CompoundTag tag = worldMapList.getCompound(d);
			RegistryKey<World> worldID;
			worldID = RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString(TAG_WORLD_ID)));

			Map<ShortVec2, Identifier> biomeMap = getTiles(worldID);
			ShortVec2 coords = new ShortVec2(tag.getInt("x"), tag.getInt("y"));
			biomeMap.put(coords, Identifier.tryParse(tag.getString("id")));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		compound.putInt(TAG_VERSION, VERSION);
		ListTag worldMaplist = new ListTag();
		for (RegistryKey<World> world : worldMap.keySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putString(TAG_WORLD_ID, world.getValue().toString());

			Map<ShortVec2, Identifier> biomeMap = getTiles(world);

			for (Entry<ShortVec2, Identifier> entry : biomeMap.entrySet()) {
				tag.putInt("x", entry.getKey().x);
				tag.putInt("y", entry.getKey().y);
				tag.putString("id", entry.getValue().toString());
			}

			worldMaplist.add(tag);
		}
		compound.put(TAG_WORLD_MAP_LIST, worldMaplist);
		
		return compound;
	}
	
	private Map<ShortVec2, Identifier> getTiles(RegistryKey<World> world) {
		return worldMap.computeIfAbsent(world,
				k -> new ConcurrentHashMap<>(2, 0.75f, 2));
	}
	
	/** If no custom tile is set at the specified coordinates, returns -1. */
	public Identifier getTile(RegistryKey<World> world, int x, int z) {
		return getTiles(world).get(tempCoords.set(x, z));
	}
	
	/** If setting tile on the server, a packet should be sent to all players. */
	public void setTile(RegistryKey<World> world, int x, int z, Identifier biome) {
		getTiles(world).put(new ShortVec2(x, z), biome);
		markDirty();
	}
	
	public void removeTile(RegistryKey<World> world, int x, int z) {
		getTiles(world).remove(tempCoords.set(x, z));
		markDirty();
	}
	
	/** Send all data to player in several zipped packets. */
	public void syncOnPlayer(PlayerEntity player) {
		for (RegistryKey<World> world : worldMap.keySet()) {
			Map<ShortVec2, Identifier> biomes = getTiles(world);

			new CustomTileInfoS2CPacket(world, biomes).send((ServerPlayerEntity) player);
		}

		Log.info("Sent custom biome data to player %s", player.getCommandSource().getName());
	}

}
