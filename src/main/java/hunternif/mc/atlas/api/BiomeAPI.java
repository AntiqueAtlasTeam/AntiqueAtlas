package hunternif.mc.atlas.api;

import hunternif.mc.atlas.client.StandardTextureSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** API for custom biomes. All methods are for the client side only.
 * <p>Methods accepting BiomeGenBase and integer biome ID are equivalent.</p>
 * <p>Methods accepting arrays of ResourceLocations will produce the same result
 * as the ones accepting StandardTextureSet if you supplied the same textures that
 * constitute the set. Only in case of the set only its name is written to the
 * config; otherwise a complete list of texture files is written.</p> */
public interface BiomeAPI {
	/** Version of Biome API, meaning this particular class. */
	int getVersion();
	
	/** Assign texture to biome ID. The textures will be added as variations. */
	@SideOnly(Side.CLIENT)
	void setTexture(int biomeID, ResourceLocation ... textures);
	
	/** Assign texture to biome. The textures will be added as variations. */
	@SideOnly(Side.CLIENT)
	void setTexture(BiomeGenBase biome, ResourceLocation ... textures);
	
	/** Assign texture set to biome ID. The textures in the set will be added as a variations. */
	@SideOnly(Side.CLIENT)
	void setTexture(int biomeID, StandardTextureSet textureSet);
	
	/** Assign texture set to biome. The textures in the set will be added as a variations. */
	@SideOnly(Side.CLIENT)
	void setTexture(BiomeGenBase biome, StandardTextureSet textureSet);
	
	/** Assigns texture to biome ID, if this biome has no texture assigned.
	 * Returns true if the texture was changed. */
	@SideOnly(Side.CLIENT)
	boolean setTextureIfNone(int biomeID, ResourceLocation ... textures);
	
	/** Assigns texture to biome, if this biome has no texture assigned.
	 * Returns true if the texture was changed. */
	@SideOnly(Side.CLIENT)
	boolean setTextureIfNone(BiomeGenBase biome, ResourceLocation ... textures);
	
	/** Assigns texture set to biome ID, if this biome has no texture assigned.
	 * Returns true if the texture was changed. */
	@SideOnly(Side.CLIENT)
	boolean setTextureIfNone(int biomeID, StandardTextureSet textureSet);
	
	/** Assigns texture set to biome, if this biome has no texture assigned.
	 * Returns true if the texture was changed. */
	@SideOnly(Side.CLIENT)
	boolean setTextureIfNone(BiomeGenBase biome, StandardTextureSet textureSet);
	
	/** Save the biome ID config file. You might want to avoid saving if no
	 * texture has actually been changed, so that the config file is not
	 * overwritten too often (that makes it easier to modify manually). */
	@SideOnly(Side.CLIENT)
	void save();
}
