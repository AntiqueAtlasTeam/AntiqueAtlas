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
import net.minecraft.world.dimension.DimensionType;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This world-saved data contains all the custom pseudo-biome IDs in a world.
 * Atlases check with it when updating themselves.
 * @author Hunternif
 */
public class ExtBiomeData extends PersistentState {
	private static final int VERSION = 1;
	private static final String TAG_VERSION = "aaVersion";
	private static final String TAG_WORLD_MAP_LIST = "dimMap";
	private static final String TAG_WORLD_ID = "worldID";
	private static final String TAG_BIOME_IDS = "biomeIDs";
	
	public ExtBiomeData(String key) {
		super(key);
	}
	
	private final Map<RegistryKey<World>, Map<ShortVec2, Integer>> dimensionMap =
            new ConcurrentHashMap<>(2, 0.75f, 2);
	
	private final ShortVec2 tempCoords = new ShortVec2(0, 0);

	@Override
	public void fromTag(CompoundTag compound) {
		int version = compound.getInt(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}
		ListTag dimensionMapList = compound.getList(TAG_WORLD_MAP_LIST, NbtType.COMPOUND);
		for (int d = 0; d < dimensionMapList.size(); d++) {
			CompoundTag tag = dimensionMapList.getCompound(d);
			RegistryKey<World> worldID;
			worldID = RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString(TAG_WORLD_ID)));

			Map<ShortVec2, Integer> biomeMap = getBiomesInWorld(worldID);
			int[] intArray = tag.getIntArray(TAG_BIOME_IDS);
			for (int i = 0; i < intArray.length; i += 3) {
				ShortVec2 coords = new ShortVec2(intArray[i], intArray[i+1]);
				biomeMap.put(coords, intArray[i + 2]);
			}}
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		compound.putInt(TAG_VERSION, VERSION);
		ListTag dimensionMapList = new ListTag();
		for (RegistryKey<World> world : dimensionMap.keySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putString(TAG_WORLD_ID, world.getValue().toString());
			Map<ShortVec2, Integer> biomeMap = getBiomesInWorld(world);
			int[] intArray = new int[biomeMap.size()*3];
			int i = 0;
			for (Entry<ShortVec2, Integer> entry : biomeMap.entrySet()) {
				intArray[i++] = entry.getKey().x;
				intArray[i++] = entry.getKey().y;
				intArray[i++] = entry.getValue();
			}
			tag.putIntArray(TAG_BIOME_IDS, intArray);
			dimensionMapList.add(tag);
		}
		compound.put(TAG_WORLD_MAP_LIST, dimensionMapList);
		
		return compound;
	}
	
	private Map<ShortVec2, Integer> getBiomesInWorld(RegistryKey<World> world) {
		return dimensionMap.computeIfAbsent(world,
				k -> new ConcurrentHashMap<>(2, 0.75f, 2));
	}
	
	/** If no custom tile is set at the specified coordinates, returns -1. */
	public int getBiomeAt(RegistryKey<World> world, int x, int z) {
		Integer i = getBiomesInWorld(world).get(tempCoords.set(x, z));
		return i != null ? i : -1;
	}
	
	/** If setting biome on the server, a packet should be sent to all players. */
	public void setBiomeAt(RegistryKey<World> world, int x, int z, int biome) {
		getBiomesInWorld(world).put(new ShortVec2(x, z), biome);
		markDirty();
	}
	
	public void removeBiomeAt(RegistryKey<World> dimension, int x, int z) {
		getBiomesInWorld(dimension).remove(tempCoords.set(x, z));
		markDirty();
	}
	
	/** Send all data to player in several zipped packets. */
	public void syncOnPlayer(PlayerEntity player) {
		for (RegistryKey<World> world : dimensionMap.keySet()) {
			Map<ShortVec2, Integer> biomes = getBiomesInWorld(world);

			new CustomTileInfoS2CPacket(world, biomes).send((ServerPlayerEntity) player);
		}

		Log.info("Sent custom biome data to player %s", player.getCommandSource().getName());
	}

}
