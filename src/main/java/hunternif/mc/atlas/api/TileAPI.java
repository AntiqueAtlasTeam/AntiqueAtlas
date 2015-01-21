package hunternif.mc.atlas.api;

import hunternif.mc.atlas.client.TextureSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * API for custom tiles, i.e. dungeons, towns etc. Texture methods are strictly
 * for the client side.
 * @author Hunternf
 */
public interface TileAPI {
	/** Version of Tile API, meaning this particular interface. */
	public static final int VERSION = 3;
	
	/** Assign one or more textures to a unique tile name, implicitly creating
	 * a new texture set using the same unique name.
	 * The different textures in the array will be added as variations, and each
	 * individual texture name will be saved in the config file.
	 * Client-side only! */
	@SideOnly(Side.CLIENT)
	void setTexture(String uniqueTileName, ResourceLocation ... textures);
	
	/** Assign a texture set to a unique tile name.
	 * The different textures in the set will be added as variations, and only
	 * the name of the texture set will be saved in the config file.
	 * Client-side only! */
	@SideOnly(Side.CLIENT)
	void setTexture(String uniqueTileName, TextureSet textureSet);
	
	/**
	 * <p><b>Not yet implemented.</b></p>
	 * 
	 * Put the specified custom tile at the specified chunk coordinates
	 * in the specified atlas.
	 * You only need to call this method once for every chunk, after that
	 * the tile will be persisted with the world and loaded when the server
	 * starts up.
	 * <p>
	 * If calling this method on the client, the player must carry the atlas
	 * in his inventory, to prevent griefing!
	 * </p>
	 * <p>
	 * For custom biomes or for altering biomes at specific chunks, see
	 * {@link BiomeAPI#setBiome}
	 * </p>
	 * 
	 * @param world		dimension the chunk is located in.
	 * @param atlasID	the ID of the atlas you want to put marker in. Equal
	 * 					to ItemStack damage for ItemAtlas.
	 * @param tileName	the unique name for your tile type. You must use the
	 * 					same when registering the texture.
	 * @param chunkX	x chunk coordinate. (block coordinate >> 4)
	 * @param chunkZ	z chunk coordinate. (block coordinate >> 4)
	 */
	void putCustomTile(World world, int atlasID, String tileName, int chunkX, int chunkZ);
	
	/**
	 * Put the specified custom tile at the specified chunk coordinates
	 * globally i.e. in every atlas. Therefore this method has to be called
	 * on the <b>server</b> only!
	 * You only need to call this method once for every chunk, after that
	 * the tile will be persisted with the world and loaded when the server
	 * starts up.
	 * 
	 * @param world		dimension the chunk is located in.
	 * @param tileName	the unique name for your tile type. You must use the
	 * 					same when registering the texture.
	 * @param chunkX	x chunk coordinate. (block coordinate >> 4)
	 * @param chunkZ	z chunk coordinate. (block coordinate >> 4)
	 */
	void putCustomGlobalTile(World world, String tileName, int chunkX, int chunkZ);
}
