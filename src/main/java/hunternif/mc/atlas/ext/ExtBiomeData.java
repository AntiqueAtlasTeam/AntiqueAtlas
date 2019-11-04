package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.IntTilesPacket;
import hunternif.mc.atlas.network.client.ShortTilesPacket;
import hunternif.mc.atlas.network.client.TilesPacket;
import hunternif.mc.atlas.util.IntVec2;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.MathUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

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
	
	private final Map<Integer /*dimension ID*/, Map<IntVec2, Integer /*biome ID*/>> dimensionMap =
            new ConcurrentHashMap<>(2, 0.75f, 2);
	
	private final IntVec2 tempCoords = new IntVec2(0, 0);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		int version = compound.getInteger(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}
		NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST, Constants.NBT.TAG_COMPOUND);
		for (int d = 0; d < dimensionMapList.tagCount(); d++) {
			NBTTagCompound tag = dimensionMapList.getCompoundTagAt(d);
			int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
			Map<IntVec2, Integer> biomeMap = getBiomesInDimension(dimensionID);
			int[] intArray = tag.getIntArray(TAG_BIOME_IDS);
			for (int i = 0; i < intArray.length; i += 3) {
				IntVec2 coords = new IntVec2(intArray[i], intArray[i+1]);
				biomeMap.put(coords, intArray[i + 2]);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_VERSION, VERSION);
		NBTTagList dimensionMapList = new NBTTagList();
		for (Integer dimension : dimensionMap.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(TAG_DIMENSION_ID, dimension);
			Map<IntVec2, Integer> biomeMap = getBiomesInDimension(dimension);
			int[] intArray = new int[biomeMap.size()*3];
			int i = 0;
			for (Entry<IntVec2, Integer> entry : biomeMap.entrySet()) {
				intArray[i++] = entry.getKey().x;
				intArray[i++] = entry.getKey().y;
				intArray[i++] = entry.getValue();
			}
			tag.setIntArray(TAG_BIOME_IDS, intArray);
			dimensionMapList.appendTag(tag);
		}
		compound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
		
		return compound;
	}
	
	private Map<IntVec2, Integer> getBiomesInDimension(int dimension) {
		return dimensionMap.computeIfAbsent(dimension,
				k -> new ConcurrentHashMap<>(2, 0.75f, 2));
	}
	
	/** If no custom tile is set at the specified coordinates, returns -1. */
	public int getBiomeIdAt(int dimension, int x, int z) {
		Integer biomeID = getBiomesInDimension(dimension).get(tempCoords.set(x, z));
		return biomeID == null ? -1 : biomeID;
	}
	
	/** If setting biome on the server, a packet should be sent to all players. */
	public void setBiomeIdAt(int dimension, int x, int z, int biomeID) {
		getBiomesInDimension(dimension).put(new IntVec2(x, z), biomeID);
		markDirty();
	}
	
	public void removeBiomeAt(int dimension, int x, int z) {
		getBiomesInDimension(dimension).remove(tempCoords.set(x, z));
		markDirty();
	}
	
	/** Send all data to player in several zipped packets. */
	public void syncOnPlayer(EntityPlayer player) {
		for (Integer dimension : dimensionMap.keySet()) {
			Map<IntVec2, Integer> biomes = getBiomesInDimension(dimension);
			boolean useInt = biomes.keySet().stream().anyMatch(p -> MathUtil.exceedsShort(p.x, p.y));
			TilesPacket packet = useInt ? new IntTilesPacket(dimension) : new ShortTilesPacket(dimension);
			for (Entry<IntVec2, Integer> entry : biomes.entrySet()) {
				packet.addTile(entry.getKey().x, entry.getKey().y, entry.getValue());
			}
			PacketDispatcher.sendTo(packet, (EntityPlayerMP) player);
		}
		Log.info("Sent custom biome data to player %s", player.getName());
	}

}
