package hunternif.mc.atlas.api;

import net.minecraft.world.World;

import hunternif.mc.atlas.registry.MarkerType;

/**
 * API for putting custom markers to the atlases. Set the textures on the
 * client side, put markers into atlases on the server side.
 * @author Hunternif
 */
public interface MarkerAPI {
	
	/**
	 * Registers the marker type, this should be called in PreInit, as config file overrides are loaded in Init.
	 */
	void registerMarker(MarkerType markerType);
	
	/**
	 * Put a marker in the specified Atlas instance at specified block
	 * coordinates. Call this method per one marker either on the server or
	 * on the client.
	 * <p>
	 * If calling this method on the client, the player must carry the atlas
	 * in his inventory, to prevent griefing!
	 * </p>
	 * @param world
	 * @param visibleAhead	whether the marker should appear visible even if
	 * 						the player hasn't yet discovered that area.
	 * @param atlasID		the ID of the atlas you want to put marker in. Equal
	 * 						to ItemStack damage for ItemAtlas.
	 * @param markerType	name of your custom marker type.
	 * @param label			text label to be displayed on mouseover.
	 * @param x				block coordinate
	 * @param z				block coordinate
	 *
	 * @return returns the marker id. -1 if failed or client
	 */
	int putMarker(World world, boolean visibleAhead, int atlasID,
			String markerType, String label, int x, int z);
	
	/**
	 * Put a marker in all atlases in the world at specified block coordinates.
	 * Server side only!
	 * @param world
	 * @param visibleAhead	whether the marker should appear visible even if
	 * 						the player hasn't yet discovered that area.
	 * @param markerType	name of your custom marker type.
	 * @param label			text label to be displayed on mouseover.
	 * @param x				block coordinate
	 * @param z				block coordinate
	 *
	 * @return returns the marker id. -1 if failed or client
	 */
	int putGlobalMarker(World world, boolean visibleAhead,
			String markerType, String label, int x, int z);
	
	/**
	 * Delete a marker from an atlas.
	 * <p>
	 * If calling this method on the client,
	 * the player must carry the atlas in his inventory, to prevent griefing!
	 * </p>
	 * @param world
	 * @param atlasID
	 * @param markerID
	 */
	void deleteMarker(World world, int atlasID, int markerID);
	
	/**
	 * Delete a global marker from all atlases. Server side only!
	 * @param world
	 * @param markerID
	 */

	void deleteGlobalMarker(World world, int markerID);
}
