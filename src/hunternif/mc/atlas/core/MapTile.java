package hunternif.mc.atlas.core;

import hunternif.mc.atlas.gui.BiomeTextureMap;

import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;

public class MapTile {
	public byte biomeID;
	
	/** Used by randomized textures. */
	public transient byte variationNumber;
	
	// ========== Corner flags ==========
	public static final byte CONVEX = 0;
	public static final byte CONCAVE = 1;
	public static final byte HORIZONTAL = 2;
	public static final byte VERTICAL = 3;
	public static final byte FULL = 4;
	
	public transient byte topLeft = CONVEX;
	public transient byte topRight = CONVEX;
	public transient byte bottomLeft = CONVEX;
	public transient byte bottomRight = CONVEX;
	
	/** If all corners are convex, the texture must show a single object, e.g. one tree. */
	public boolean isSingleObject() {
		return topLeft == CONVEX && topRight == CONVEX && bottomLeft == CONVEX && bottomRight == CONVEX;
	}
	
	public MapTile(BiomeGenBase biome) {
		this((byte)biome.biomeID);
	}
	public MapTile(byte biomeID) {
		this.biomeID = biomeID;
		int maxVariations = BiomeTextureMap.instance().getVariations(getBiome());
		if (maxVariations <= 0) variationNumber = 0;
		else variationNumber = (byte)(new Random().nextInt(maxVariations));
	}
	
	public BiomeGenBase getBiome() {
		if (biomeID >= 0) {
			return BiomeGenBase.biomeList[biomeID];
		} else {
			return null;
		}
	}
}
