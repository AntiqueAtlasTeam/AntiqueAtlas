package hunternif.mc.atlas.api;

import hunternif.mc.atlas.client.StandardTextureSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * API for custom biomes.
 * All texture methods are for the <b>client</b> side only!
 * Methods accepting BiomeGenBase and integer biome ID are equivalent.
 * @author Hunternif
 */
public interface BiomeAPI {
	/** Version of Biome API, meaning this particular interface. */
	public static final int VERSION = 3;
	
	/** Assign one or more texture to biome ID.
	 * The different textures in the array will be added as variations, and each
	 * individual texture name will be saved in the config file. */
	@SideOnly(Side.CLIENT)
	void setTexture(int biomeID, ResourceLocation ... textures);
	
	/** Assign one or more texture to biome.
	 * The different textures in the array will be added as variations, and each
	 * individual texture name will be saved in the config file. */
	@SideOnly(Side.CLIENT)
	void setTexture(BiomeGenBase biome, ResourceLocation ... textures);
	
	/** Assign one of the standard texture sets to biome ID.
	 * The different textures in the set will be added as variations, and only
	 * the name of the texture set will be saved in the config file. */
	@SideOnly(Side.CLIENT)
	void setTexture(int biomeID, StandardTextureSet textureSet);
	
	/** Assign one of the standard texture sets to biome.
	 * The different textures in the set will be added as variations, and only
	 * the name of the texture set will be saved in the config file. */
	@SideOnly(Side.CLIENT)
	void setTexture(BiomeGenBase biome, StandardTextureSet textureSet);
	
	/**
	 * <p><b>Not yet implemented.</b></p>
	 * 
	 * Edit the biome ID at the specified chunk in the specified atlas. 
	 * You only need to call this method once for every chunk, after that
	 * the tile will be persisted with the world and loaded when the server
	 * starts up.
	 * <p>
	 * Note that global custom tiles will override biome IDs at shared chunks.
	 * </p>
	 * <p>
	 * If calling this method on the client, the player must carry the atlas
	 * in his inventory, to prevent griefing!
	 * </p>
	 * <p>
	 * For setting custom tiles that don't correspond to biomes, see
	 * {@link TileAPI#putCustomTile}
	 * </p>
	 * @param world		dimension the chunk is located in.
	 * @param atlasID	the ID of the atlas you want to put marker in. Equal
	 * 					to ItemStack damage for ItemAtlas.
	 * @param biomeID	
	 * @param chunkX	x chunk coordinate. (block coordinate >> 4)
	 * @param chunkZ	z chunk coordinate. (block coordinate >> 4)
	 */
	void setBiome(World world, int atlasID, int biomeID, int chunkX, int chunkZ);
	
	/**
	 * <p><b>Not yet implemented.</b></p>
	 * 
	 * Edit the biome at the specified chunk in the specified atlas. 
	 * You only need to call this method once for every chunk, after that
	 * the tile will be persisted with the world and loaded when the server
	 * starts up.
	 * <p>
	 * Note that global custom tiles will override biome IDs at shared chunks.
	 * </p>
	 * <p>
	 * If calling this method on the client, the player must carry the atlas
	 * in his inventory, to prevent griefing!
	 * </p>
	 * <p>
	 * For setting custom tiles that don't correspond to biomes, see
	 * {@link TileAPI#putCustomTile}
	 * </p>
	 * @param world		dimension the chunk is located in.
	 * @param atlasID	the ID of the atlas you want to put marker in. Equal
	 * 					to ItemStack damage for ItemAtlas.
	 * @param biome	
	 * @param chunkX	x chunk coordinate. (block coordinate >> 4)
	 * @param chunkZ	z chunk coordinate. (block coordinate >> 4)
	 */
	void setBiome(World world, int atlasID, BiomeGenBase biome, int chunkX, int chunkZ);
}
