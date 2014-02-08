package hunternif.mc.atlas.api;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.core.BiomeTextureMap;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** API for custom biomes. Only for the client side. */
public class BiomeAPI {
	/** Assign texture to biome ID. The textures will be added as variations. */
	@SideOnly(Side.CLIENT)
	public static void setTexture(int biomeID, ResourceLocation ... textures) {
		BiomeTextureMap.instance().setTexture(biomeID, textures);
	}
	
	/** Assign texture set to biome iD. The textures in the set will be added as a variations. */
	@SideOnly(Side.CLIENT)
	public static void setTexture(int biomeID, StandardTextureSet textureSet) {
		BiomeTextureMap.instance().setTexture(biomeID, textureSet);
	}
	
	/** Assigns texture to biome ID, if this biome has no texture assigned.
	 * Returns true if the texture was changed. */
	@SideOnly(Side.CLIENT)
	public static boolean setTextureIfNone(int biomeID, ResourceLocation ... textures) {
		return BiomeTextureMap.instance().setTextureIfNone(biomeID, textures);
	}
	
	/** Assigns texture set to biome ID, if this biome has no texture assigned.
	 * Returns true if the texture was changed. */
	@SideOnly(Side.CLIENT)
	public static boolean setTextureIfNone(int biomeID, StandardTextureSet textureSet) {
		return BiomeTextureMap.instance().setTextureIfNone(biomeID, textureSet);
	}
	
	/** Save the biome ID config file. You might want to avoid saving if no
	 * texture was actually changed, so that the config file is not overwritten
	 * too often (that makes it easier to modify manually). */
	public static void save() {
		AntiqueAtlasMod.proxy.updateTextureConfig();
	}
}
