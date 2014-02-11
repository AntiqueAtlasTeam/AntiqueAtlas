package hunternif.mc.atlas.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * API for putting custom markers to the atlases. Set the textures on the
 * client side, put markers into atlases on the server side.
 * @author Hunternif
 */
public interface MarkerAPI {
	/** Assign texture to a marker type. */
	@SideOnly(Side.CLIENT)
	void setTexture(String markerType, ResourceLocation texture);
	
	/** Assign texture to a marker type, if no texture has been assigned to it.
	 * Returns true if the texture was changed for this marker type. */
	@SideOnly(Side.CLIENT)
	boolean setTextureIfNone(String markerType, ResourceLocation texture);
	
	/** Save marker texture config file. You might want to avoid saving if no
	 * texture has actually been changed, so that the config file is not
	 * overwritten too often (that makes it easier to modify manually). */
	@SideOnly(Side.CLIENT)
	void save();
	
	/**
	 * Put a marker in the specified Atlas instance at specified block
	 * coordinates. Call this method per one marker either on the server or
	 * on the client.
	 * @param world
	 * @param dimension
	 * @param atlasID		the ID of the atlas you want to put marker in.
	 * @param markerType	name of your custom marker type.
	 * @param label			text label to be displayed on mouseover.
	 * @param x				block coordinate
	 * @param z				block coordinate
	 */
	void putMarker(World world, int dimension, int atlasID, String markerType, String label, int x, int z);
	
	/**
	 * Works similarly to {@link #putMarker(World, int, String, String, int, int)},
	 * but puts the marker into all atlases in the specified world.
	 */
	void putGlobalMarker(World world, int dimension, String markerType, String label, int x, int z);
}
