package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.ByteUtil;

import java.util.List;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ChunkBiomeAnalyzer {
	public static class BiomeFlag {
		/** Indicates that the chunk is empty or not loaded. */
		public static final int NONE = -1;
		/** Indicates a village. One of the 2 pseudo-biomes is chosen at random
		 * to make the village look more varied. */
		//TODO: add support for unlimited number of randomizations.
		public static final int VILLAGE1 = -2,
								VILLAGE2 = -3;
	}
	
	public static final ChunkBiomeAnalyzer instance = new ChunkBiomeAnalyzer();
	
	public int getChunkBiomeID(Chunk chunk) {
		if (!chunk.isChunkLoaded) return BiomeFlag.NONE;
		
		if (isVillageInChunk(chunk)) {
			return Math.random() < 0.5 ? BiomeFlag.VILLAGE1 : BiomeFlag.VILLAGE2;
		}
		
		int[] biomeOccurences = new int[256];
		for (int i = 0; i < chunk.getBiomeArray().length; i++) {
			int biomeId = ByteUtil.unsignedByteToInt(chunk.getBiomeArray()[i]);
			if (biomeId >= 0 && BiomeGenBase.biomeList[biomeId] != null) {
				if (BiomeDictionary.isBiomeOfType(BiomeGenBase.biomeList[biomeId], Type.WATER)) {
					// Water is important to show connected rivers:
					biomeOccurences[biomeId] += 4;
				} else if (BiomeDictionary.isBiomeOfType(BiomeGenBase.biomeList[biomeId], Type.SWAMP)) {
					// Swamps are often intermingled with water, but they look
					// like water themselves, so they take precedence:
					biomeOccurences[biomeId] += 6;
				} else {
					biomeOccurences[biomeId] ++;
				}
			}
		}
		int meanBiomeId = BiomeFlag.NONE;
		int meanBiomeOccurences = 0;
		for (int i = 0; i < biomeOccurences.length; i++) {
			if (biomeOccurences[i] > meanBiomeOccurences) {
				meanBiomeId = i;
				meanBiomeOccurences = biomeOccurences[i];
			}
		}
		return meanBiomeId;
	}
	
	//TODO: bug: on the client the village may not have spawned yet.
	public static boolean isVillageInChunk(Chunk chunk) {
		int centerX = (chunk.xPosition << 4) + 8;
		int centerZ = (chunk.zPosition << 4) + 8;
		List<Village> villages = chunk.worldObj.villageCollectionObj.getVillageList();
		for (Village village : villages) {
			ChunkCoordinates coords = village.getCenter();
			if ((centerX - coords.posX)*(centerX - coords.posX) + (centerZ - coords.posZ)*(centerZ - coords.posZ)
					<= village.getVillageRadius()*village.getVillageRadius()) {
				return isVillageDoorInChunk(village, chunk);
			}
		}
		return false;
	}
	
	public static boolean isVillageDoorInChunk(Village village, Chunk chunk) {
		int centerX = (chunk.xPosition << 4) + 8;
		int centerZ = (chunk.zPosition << 4) + 8;
		if (village.isAnnihilated()) {
			return true;
		}
		for (Object doorInfo : village.getVillageDoorInfoList()) {
			VillageDoorInfo door = (VillageDoorInfo) doorInfo;
			if ((centerX - door.posX)*(centerX - door.posX) + (centerZ - door.posZ)*(centerZ - door.posZ)
					<= 10*10) {
				return true;
			}
		}
		return false;
	}
}
