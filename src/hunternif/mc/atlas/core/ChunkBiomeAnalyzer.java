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
		/** Indicates a village. */
		//TODO: add support for unlimited number of randomizations.
		public static final int VILLAGE_HOUSE = -2,
								VILLAGE_TERRITORY = -3;
	}
	
	public static final ChunkBiomeAnalyzer instance = new ChunkBiomeAnalyzer();
	
	public int getChunkBiomeID(Chunk chunk) {
		if (!chunk.isChunkLoaded) return BiomeFlag.NONE;
		
		Village village = getVillageInChunk(chunk);
		if (village != null) {
			return isVillageDoorInChunk(village, chunk) ? BiomeFlag.VILLAGE_HOUSE : BiomeFlag.VILLAGE_TERRITORY;
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
	
	public static Village getVillageInChunk(Chunk chunk) {
		int centerX = (chunk.xPosition << 4) + 8;
		int centerZ = (chunk.zPosition << 4) + 8;
		List<Village> villages = chunk.worldObj.villageCollectionObj.getVillageList();
		for (Village village : villages) {
			ChunkCoordinates coords = village.getCenter();
			if ((centerX - coords.posX)*(centerX - coords.posX) + (centerZ - coords.posZ)*(centerZ - coords.posZ)
					<= village.getVillageRadius()*village.getVillageRadius()) {
				return village;
			}
		}
		return null;
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
	
	public boolean shouldSyncBiomeOnClient(int biomeID) {
		return biomeID == BiomeFlag.VILLAGE_HOUSE || biomeID == BiomeFlag.VILLAGE_TERRITORY;
	}
}
