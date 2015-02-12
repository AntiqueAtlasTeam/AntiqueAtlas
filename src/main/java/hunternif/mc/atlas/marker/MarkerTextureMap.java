package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.util.SaveData;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Maps marker type to texture. <i>Not thread-safe!</i>
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class MarkerTextureMap extends SaveData {
	private static final MarkerTextureMap INSTANCE = new MarkerTextureMap();
	public static MarkerTextureMap instance() {
		return INSTANCE;
	}
	
	/** Marker types are sorted by their name. */
	final SortedMap<String, ResourceLocation> textureMap = new TreeMap<String, ResourceLocation>();
	public static final ResourceLocation defaultTexture = Textures.MARKER_RED_X_SMALL;
	
	public void setTexture(String markerType, ResourceLocation texture) {
		ResourceLocation oldTexture = textureMap.get(markerType);
		if (!texture.equals(oldTexture)) {
			textureMap.put(markerType, texture);
			markDirty();
		}
	}
	
	/** If no texture has been registered, returns default texture. */
	public ResourceLocation getTexture(String markerType) {
		ResourceLocation texture = textureMap.get(markerType);
		return texture == null ? defaultTexture : texture;
	}
	
	public boolean isRegistered(String markerType) {
		return textureMap.containsKey(markerType);
	}
	
	public Collection<String> getAllTypes() {
		return textureMap.keySet();
	}
	
	public Collection<ResourceLocation> getAllTextures() {
		return textureMap.values();
	}
}
