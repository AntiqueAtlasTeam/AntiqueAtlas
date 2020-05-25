package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TilesPacket;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This world-saved data contains all the custom pseudo-biome IDs in a world.
 * Atlases check with it when updating themselves.
 * @author Hunternif
 */
public class ExtBiomeData extends WorldSavedData {
	private static final int VERSION = 1;
	private static final String TAG_VERSION = "aaVersion";
	private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
	private static final String TAG_DIMENSION_ID = "dimID";
	private static final String TAG_BIOME_IDS = "biomeIDs";
	
	public ExtBiomeData(String key) {
		super(key);
	}
	
	private final Map<DimensionType /*dimension ID*/, Map<ShortVec2, Integer>> dimensionMap =
            new ConcurrentHashMap<>(2, 0.75f, 2);
	
	private final ShortVec2 tempCoords = new ShortVec2(0, 0);

	@Override
	public void read(CompoundNBT compound) {
		int version = compound.getInt(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}
		ListNBT dimensionMapList = compound.getList(TAG_DIMENSION_MAP_LIST, Constants.NBT.TAG_COMPOUND);
		for (int d = 0; d < dimensionMapList.size(); d++) {
			CompoundNBT tag = dimensionMapList.getCompound(d);
			DimensionType dimensionID;
			if (tag.contains(TAG_DIMENSION_ID, Constants.NBT.TAG_ANY_NUMERIC)) {
				dimensionID = Registry.DIMENSION_TYPE.getByValue(tag.getInt(TAG_DIMENSION_ID));
			} else {
				dimensionID = Registry.DIMENSION_TYPE.getOrDefault(new ResourceLocation(tag.getString(TAG_DIMENSION_ID)));
			}
			Map<ShortVec2, Integer> biomeMap = getBiomesInDimension(dimensionID);
			int[] intArray = tag.getIntArray(TAG_BIOME_IDS);
			for (int i = 0; i < intArray.length; i += 3) {
				ShortVec2 coords = new ShortVec2(intArray[i], intArray[i+1]);
				biomeMap.put(coords, intArray[i + 2]);
			}}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt(TAG_VERSION, VERSION);
		ListNBT dimensionMapList = new ListNBT();
		for (DimensionType dimension : dimensionMap.keySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString(TAG_DIMENSION_ID, Registry.DIMENSION_TYPE.getKey(dimension).toString());
			Map<ShortVec2, Integer> biomeMap = getBiomesInDimension(dimension);
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
		compound.put(TAG_DIMENSION_MAP_LIST, dimensionMapList);
		
		return compound;
	}
	
	private Map<ShortVec2, Integer> getBiomesInDimension(DimensionType dimension) {
		return dimensionMap.computeIfAbsent(dimension,
				k -> new ConcurrentHashMap<>(2, 0.75f, 2));
	}
	
	/** If no custom tile is set at the specified coordinates, returns -1. */
	public int getBiomeAt(DimensionType dimension, int x, int z) {
		Integer i = getBiomesInDimension(dimension).get(tempCoords.set(x, z));
		return i != null ? i : -1;
	}
	
	/** If setting biome on the server, a packet should be sent to all players. */
	public void setBiomeAt(DimensionType dimension, int x, int z, int biome) {
		getBiomesInDimension(dimension).put(new ShortVec2(x, z), biome);
		markDirty();
	}
	
	public void removeBiomeAt(DimensionType dimension, int x, int z) {
		getBiomesInDimension(dimension).remove(tempCoords.set(x, z));
		markDirty();
	}
	
	/** Send all data to player in several zipped packets. */
	public void syncOnPlayer(PlayerEntity player) {
		for (DimensionType dimension : dimensionMap.keySet()) {
			TilesPacket packet = new TilesPacket(dimension);
			Map<ShortVec2, Integer> biomes = getBiomesInDimension(dimension);
			for (Entry<ShortVec2, Integer> entry : biomes.entrySet()) {
				packet.addTile(entry.getKey().x, entry.getKey().y, entry.getValue());
			}
			PacketDispatcher.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), packet);
		}
		Log.info("Sent custom biome data to player %s", player.getCommandSource().getName());
	}

}
