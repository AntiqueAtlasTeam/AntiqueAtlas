package hunternif.mc.atlas.api;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.core.BiomeTextureMap;
import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** API for custom tiles, i.e. dungeons, towns etc. */
public class TileAPI {
	/**
	 * Because pseudo-biome IDs have to be synced with the server, they may not
	 * have been initialized when the texture registration methods are called on
	 * the client. In that case the textures are put in this map to be later
	 * registered when the server sends the packet with pseudo-biome ID for the
	 * corresponding unique name.
	 * <p>This map maps unique tile name to a StandardTextureSet or an array of
	 * ResourceLocations of textures.</p>
	 */
	private static final Map<String, Object> pendingTextures = new HashMap<String, Object>();
	
	/** Assign texture to tile. The textures will be added as variations. */
	@SideOnly(Side.CLIENT)
	public static void setTexture(String uniqueTileName, ResourceLocation ... textures) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			BiomeTextureMap.instance().setTexture(id, textures);
		} else {
			pendingTextures.put(uniqueTileName, textures);
		}
	}
	
	/** Assign texture set to tile. */
	@SideOnly(Side.CLIENT)
	public static void setTexture(String uniqueTileName, StandardTextureSet textureSet) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			BiomeTextureMap.instance().setTexture(id, textureSet);
		} else {
			pendingTextures.put(uniqueTileName, textureSet);
		}
	}
	
	/** Assigns texture to tile, if this tile has no texture assigned. */
	@SideOnly(Side.CLIENT)
	public static void setTextureIfNone(String uniqueTileName, ResourceLocation ... textures) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			BiomeTextureMap.instance().setTexture(id, textures);
		} else {
			pendingTextures.put(uniqueTileName, textures);
		}
	}
	
	/** Assigns texture set to tile, if this tile has no texture assigned. */
	@SideOnly(Side.CLIENT)
	public static void setTextureIfNone(String uniqueTileName, StandardTextureSet textureSet) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			BiomeTextureMap.instance().setTexture(id, textureSet);
		} else {
			pendingTextures.put(uniqueTileName, textureSet);
		}
	}
	
	/**
	 * Put the specified custom tile at the specified chunk coordinates. This
	 * method should only be called on the server, then the change is
	 * automatically sent to all clients.
	 * @param world		
	 * @param dimension	dimension the chunk is located in.
	 * @param tileName	the unique name you registered your tile with.
	 * @param chunkX	x chunk coordinate. (block coordinate << 4)
	 * @param chunkZ	z chunk coordinate. (block coordinate << 4)
	 */
	public static void putCustomTile(World world, int dimension, String tileName, int chunkX, int chunkZ) {
		int id = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
		AntiqueAtlasMod.extBiomeData.getData().setBiomeIdAt(dimension, id, new ShortVec2(chunkX, chunkZ));
		if (!world.isRemote) {
			//TODO send packet to all clients
		}
	}
}
