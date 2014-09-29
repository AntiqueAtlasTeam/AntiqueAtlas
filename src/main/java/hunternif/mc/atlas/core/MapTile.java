package hunternif.mc.atlas.core;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MapTile {
	public final int biomeID;
	
	/** Used for randomizing textures. */
	private transient byte variationNumber;
	
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
	
	public MapTile(int biomeID) {
		this.biomeID = biomeID;
	}
	
	/** Chooses a random texture from the set of texture variations registered
	 * for this biome ID. */
	@SideOnly(Side.CLIENT)
	public void randomizeTexture() {
		int maxVariations = BiomeTextureMap.instance().getVariations(biomeID);
		if (maxVariations <= 0) variationNumber = 0;
		else variationNumber = (byte)(new Random().nextInt(maxVariations));
	}
	
	@SideOnly(Side.CLIENT)
	public int getVariationNumber() {
		return variationNumber;
	}
	
	@Override
	public String toString() {
		return "tile" + biomeID;
	}
}
