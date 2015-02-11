package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.SaveData;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Maps unique tile name to texture set.
 * When the server sends a tile ID, the corresponding texture set is
 * re-registered into {@link BiomeTextureMap}.
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class ExtTileTextureMap extends SaveData {
	private static final ExtTileTextureMap INSTANCE = new ExtTileTextureMap();
	public static ExtTileTextureMap instance() {
		return INSTANCE;
	}
	
	final Map<String, TextureSet> textureMap = new HashMap<String, TextureSet>();
	
	public void setTexture(String tileName, TextureSet textureSet) {
		if (textureSet == null) {
			Log.error("Texture set is null!");
			return;
		}
		TextureSet previous = textureMap.put(tileName, textureSet);
		// If the old texture set is equal to the new one (i.e. has equal name
		// and equal texture files), then there's no need to update the config.
		if (previous == null) {
			markDirty();
		} else if (!previous.equals(textureSet)) {
			Log.warn("Overwriting texture set for tile \"%s\"", tileName);
			markDirty();
		}
	}
	
	/** If a texture set is not found, returns the default one from
	 * {@link BiomeTextureMap}. */
	public TextureSet getTexture(String tileName) {
		TextureSet textureSet = textureMap.get(tileName);
		return textureSet == null ? BiomeTextureMap.defaultTexture : textureSet;
	}
}
