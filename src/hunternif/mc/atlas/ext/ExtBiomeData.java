package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

/**
 * This world-saved data contains all the custom pseudo-biome IDs in a world.
 * Atlases check with it when updating themselves.
 * @author Hunternif
 */
public class ExtBiomeData extends WorldSavedData {
	private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
	private static final String TAG_DIMENSION_ID = "dimID";
	private static final String TAG_BIOME_IDS = "biomeIDs";

	public ExtBiomeData(String key) {
		super(key);
	}
	
	private Map<Integer /*dimension ID*/, Map<ShortVec2, Integer /*biome ID*/>> dimensionMap =
			new ConcurrentHashMap<Integer, Map<ShortVec2, Integer /*biome ID*/>>();

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST);
		for (int d = 0; d < dimensionMapList.tagCount(); d++) {
			NBTTagCompound tag = (NBTTagCompound) dimensionMapList.tagAt(d);
			int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
			Map<ShortVec2, Integer> biomeMap = getBiomesInDimension(dimensionID);
			int[] intArray = tag.getIntArray(TAG_BIOME_IDS);
			for (int i = 0; i < intArray.length; i += 3) {
				ShortVec2 coords = new ShortVec2(intArray[i], intArray[i+1]);
				biomeMap.put(coords, Integer.valueOf(intArray[i+2]));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList dimensionMapList = new NBTTagList();
		for (Integer dimension : dimensionMap.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(TAG_DIMENSION_ID, dimension);
			Map<ShortVec2, Integer> biomeMap = getBiomesInDimension(dimension);
			int[] intArray = new int[biomeMap.size()*3];
			int i = 0;
			for (Entry<ShortVec2, Integer> entry : biomeMap.entrySet()) {
				intArray[i++] = entry.getKey().x;
				intArray[i++] = entry.getKey().y;
				intArray[i++] = entry.getValue();
			}
			tag.setIntArray(TAG_BIOME_IDS, intArray);
			dimensionMapList.appendTag(tag);
		}
		compound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
	}
	
	private Map<ShortVec2, Integer> getBiomesInDimension(int dimension) {
		Map<ShortVec2, Integer> map = dimensionMap.get(Integer.valueOf(dimension));
		if (map == null) {
			map = new ConcurrentHashMap<ShortVec2, Integer>();
			dimensionMap.put(Integer.valueOf(dimension), map);
		}
		return map;
	}
	
	/** If no custom tile is set at the specified coordinates, returns -1. */
	public int getBiomeIdAt(int dimension, ShortVec2 coords) {
		Integer biomeID = getBiomesInDimension(dimension).get(coords);
		return biomeID == null ? ChunkBiomeAnalyzer.NOT_FOUND : biomeID;
	}
	
	public void setBiomeIdAt(int dimension, int biomeID, ShortVec2 coords) {
		getBiomesInDimension(dimension).put(coords, biomeID);
		markDirty();
	}

}
