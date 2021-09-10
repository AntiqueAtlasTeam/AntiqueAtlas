package hunternif.mc.api;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * API for tiles, such as biomes and custom (i.e. dungeons, towns etc.)
 *
 * @author Hunternf
 */
public interface TileAPI {

    /**
     * Edit the tile at the specified chunk in the specified atlas.
     * You only need to call this method once for every chunk, after that
     * the tile will be persisted with the world and loaded when the server
     * starts up.
     * <p>
     * Note that global custom tiles, such as village territory, will override
     * tile IDs at shared chunks.
     * </p>
     * <p>
     * If calling this method on the client, the player must carry the atlas
     * in his inventory, to prevent griefing!
     * </p>
     *
     * @param world   dimension the chunk is located in.
     * @param atlasID the ID of the atlas you want to put marker in. Equal
     *                to ItemStack damage for ItemAtlas.
     * @param tile    the identifier of the new tile
     * @param chunkX  x chunk coordinate. (block coordinate >> 4)
     * @param chunkZ  z chunk coordinate. (block coordinate >> 4)
     */
    void putTile(World world, int atlasID, Identifier tile, int chunkX, int chunkZ);

    /**
     * Read the tile at the specified chunk in the specified atlas.
     * <p>
     * Note that global tiles, such as village territory, will override
     * tile IDs at shared chunks in the Atlas, however this returns only
     * local tiles.
     * </p>
     *
     * @param world   dimension the chunk is located in.
     * @param atlasID the ID of the atlas you want to put marker in. Equal
     *                to ItemStack damage for ItemAtlas.
     * @param chunkX  x chunk coordinate. (block coordinate >> 4)
     * @param chunkZ  z chunk coordinate. (block coordinate >> 4)
     * @return the identifier of the requested tile
     */
    Identifier getTile(World world, int atlasID, int chunkX, int chunkZ);

    /**
     * Put the specified custom tile at the specified chunk coordinates
     * globally i.e. in every atlas. Therefore this method has to be called
     * on the <b>server</b> only!
     * You only need to call this method once for every chunk, after that
     * the tile will be persisted with the world and loaded when the server
     * starts up.
     *
     * @param world    dimension the chunk is located in.
     * @param tile     the unique name for your tile type.
     * @param chunkX   x chunk coordinate. (block coordinate >> 4)
     * @param chunkZ   z chunk coordinate. (block coordinate >> 4)
     */
    void putGlobalTile(World world, Identifier tile, int chunkX, int chunkZ);

    /**
     * Get the global tile at the specified chunk coordinates
     * i.e. in every atlas.
     *
     * @param world    dimension the chunk is located in.
     * @param chunkX   x chunk coordinate. (block coordinate >> 4)
     * @param chunkZ   z chunk coordinate. (block coordinate >> 4)
     * @return the identifier of the requested tile
     */
    Identifier getGlobalTile(World world, int chunkX, int chunkZ);

    /**
     * Delete the global tile at the specified chunk coordinates if a tile has
     * been previously put there by {@link #putGlobalTile}.
     * This method has to be called on the <b>server</b> only!
     *
     * @param world  dimension the chunk is located in.
     * @param chunkX x chunk coordinate. (block coordinate >> 4)
     * @param chunkZ z chunk coordinate. (block coordinate >> 4)
     */
    void deleteGlobalTile(World world, int chunkX, int chunkZ);
}
