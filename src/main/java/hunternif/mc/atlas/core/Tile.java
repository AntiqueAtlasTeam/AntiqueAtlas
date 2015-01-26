package hunternif.mc.atlas.core;

import org.apache.commons.lang3.RandomUtils;

/**
 * Contains information about the biome and - on the client - the variation
 * number of the biome's texture set.
 * @author Hunternif
 */
public class Tile {
	public final int biomeID;
	
	/** Used for randomizing textures.
	 * Takes on values from 0 to {@link Short#MAX_VALUE} - 1. */
	private transient short variationNumber;
	
	public Tile(int biomeID) {
		this(biomeID, (byte)0);
		randomizeTexture();
	}
	public Tile(int biomeID, byte variationNumber) {
		this.biomeID = biomeID;
		this.variationNumber = variationNumber;
	}
	
	/** Set variation number to a random byte. */
	public void randomizeTexture() {
		this.variationNumber = (short)RandomUtils.nextInt(0, Short.MAX_VALUE);
	}
	
	public short getVariationNumber() {
		return variationNumber;
	}
	
	@Override
	public String toString() {
		return "tile" + biomeID;
	}
}
