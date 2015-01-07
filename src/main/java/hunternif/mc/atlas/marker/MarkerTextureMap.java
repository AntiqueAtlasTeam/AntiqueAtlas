package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.client.Textures;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Maps marker type to texture. <i>Not thread-safe!</i>
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public enum MarkerTextureMap {
	INSTANCE;
	public static MarkerTextureMap instance() {
		return INSTANCE;
	}
	
	private final Map<String, ResourceLocation> map = new HashMap<String, ResourceLocation>();
	private final ResourceLocation defaultTexture = Textures.MARKER_RED_X_SMALL;
	
	public void setTexture(String markerType, ResourceLocation texture) {
		map.put(markerType, texture);
	}
	
	public boolean setTextureIfNone(String markerType, ResourceLocation texture) {
		if (map.containsKey(markerType)) {
			return false;
		}
		map.put(markerType, texture);
		return true;
	}
	
	/** If no texture has been registered, returns default texture. */
	public ResourceLocation getTexture(String markerType) {
		ResourceLocation texture = map.get(markerType);
		return texture == null ? defaultTexture : texture;
	}
	
	Map<String, ResourceLocation> getMap() {
		return map;
	}
	
	public Collection<ResourceLocation> getAllTextures() {
		return map.values();
	}
}
