package hunternif.mc.atlas.api;

import hunternif.mc.atlas.client.StandardTextureSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** API for custom tiles, i.e. dungeons, towns etc. Texture methods are only for
 * the client side. */
public interface TileAPI {
	/** Assign texture to tile. The textures will be added as variations. */
	@SideOnly(Side.CLIENT)
	void setTexture(String uniqueTileName, ResourceLocation ... textures);
	
	/** Assign texture set to tile. */
	@SideOnly(Side.CLIENT)
	void setTexture(String uniqueTileName, StandardTextureSet textureSet);
	
	/** Assigns texture to tile, if this tile has no texture assigned. */
	@SideOnly(Side.CLIENT)
	void setTextureIfNone(String uniqueTileName, ResourceLocation ... textures);
	
	/** Assigns texture set to tile, if this tile has no texture assigned. */
	@SideOnly(Side.CLIENT)
	void setTextureIfNone(String uniqueTileName, StandardTextureSet textureSet);
	
	/**
	 * Put the specified custom tile at the specified chunk coordinates. This
	 * method should only be called on the server, then the change is
	 * automatically sent to all clients. You only need to call this method
	 * once for every chunk, after that the tile will be persisted with the
	 * world.
	 * @param world		
	 * @param dimension	dimension the chunk is located in.
	 * @param tileName	the unique name for your tile. You must use the same
	 * 			name when setting textures for it.
	 * @param chunkX	x chunk coordinate. (block coordinate >> 4)
	 * @param chunkZ	z chunk coordinate. (block coordinate >> 4)
	 */
	void putCustomTile(World world, int dimension, String tileName, int chunkX, int chunkZ);
}
