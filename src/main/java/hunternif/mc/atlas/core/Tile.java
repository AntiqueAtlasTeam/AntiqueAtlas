package hunternif.mc.atlas.core;

import hunternif.mc.atlas.client.BiomeTextureMap;

import java.util.Random;

/**
 * Contains information about the biome and - on the client - the variation
 * number of the biome's texture set.
 * @author Hunternif
 */
public class Tile {
	public final int biomeID;
	
	/** Used for randomizing textures. */
	//TODO persist the variation as a float value so that it is independent of local 
	// Do this in the next version of the save format.
	// To save space, the float could be replaced with an integer from a sufficiently large set.
	private transient byte variationNumber;
	
	public Tile(int biomeID) {
		this.biomeID = biomeID;
	}
	
	/** Chooses a random texture from the set of texture variations registered
	 * for this biome ID. */
	public void randomizeTexture() {
		int maxVariations = BiomeTextureMap.instance().getVariations(biomeID);
		if (maxVariations <= 0) variationNumber = 0;
		else variationNumber = (byte)(new Random().nextInt(maxVariations));
	}
	
	public int getVariationNumber() {
		return variationNumber;
	}
	
	@Override
	public String toString() {
		return "tile" + biomeID;
	}
}
