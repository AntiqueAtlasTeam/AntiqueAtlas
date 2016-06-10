package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.util.SaveData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	final Map<String, MarkerTypeData> typeData = new HashMap<String, MarkerTypeData>();
	public static final ResourceLocation defaultTexture = Textures.MARKER_RED_X_SMALL;
	public static final MarkerTypeData defaultTypeData = new MarkerTypeData(true, false, false);
	
	public void setMarkerTypeData(String markerType, MarkerTypeData data) {
		MarkerTypeData oldData = typeData.get(markerType);
		if(data == oldData)
			return;
		
		if(data == null && oldData != null) {
			typeData.remove(markerType);
			markDirty();
		}
		
		if(!data.equals(oldData)) {
			typeData.put(markerType, data);
			markDirty();
		}
	}
	
	public MarkerTypeData getMarkerTypeData(String markerType) {
		MarkerTypeData data = typeData.get(markerType);
		return data == null ? defaultTypeData : data;
	}
	
	public boolean hasTileData(String markerType) {
		return typeData.containsKey(markerType);
	}
	
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
