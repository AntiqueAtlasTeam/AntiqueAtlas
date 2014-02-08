package hunternif.mc.atlas.api;

import hunternif.mc.atlas.client.StandardTextureSet;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** API for custom biomes. Only for the client side. */
public interface BiomeAPI {
	/** Assign texture to biome ID. The textures will be added as variations. */
	@SideOnly(Side.CLIENT)
	void setTexture(int biomeID, ResourceLocation ... textures);
	
	/** Assign texture set to biome iD. The textures in the set will be added as a variations. */
	@SideOnly(Side.CLIENT)
	void setTexture(int biomeID, StandardTextureSet textureSet);
	
	/** Assigns texture to biome ID, if this biome has no texture assigned.
	 * Returns true if the texture was changed. */
	@SideOnly(Side.CLIENT)
	boolean setTextureIfNone(int biomeID, ResourceLocation ... textures);
	
	/** Assigns texture set to biome ID, if this biome has no texture assigned.
	 * Returns true if the texture was changed. */
	@SideOnly(Side.CLIENT)
	boolean setTextureIfNone(int biomeID, StandardTextureSet textureSet);
	
	/** Save the biome ID config file. You might want to avoid saving if no
	 * texture was actually changed, so that the config file is not overwritten
	 * too often (that makes it easier to modify manually). */
	void save();
}
