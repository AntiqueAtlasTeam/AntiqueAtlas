package hunternif.mc.atlas.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.util.ResourceLocation;
import hunternif.mc.atlas.util.SaveData;

/**
 * Maps texture sets to their names.
 * @author Hunternif
 */
public class TextureSetMap extends SaveData {
	private static final TextureSetMap INSTANCE = new TextureSetMap();
	public static final TextureSetMap instance() {
		return INSTANCE;
	}
	
	private final Map<String, TextureSet> map = new HashMap<String, TextureSet>();
	
	public void register(TextureSet set) {
		map.put(set.name, set);
		markDirty();
	}
	
	/** Legacy support. Creates a new texture set with a UUID-based name. */
	public TextureSet createAndRegister(ResourceLocation ... textures) {
		TextureSet set = new TextureSet(UUID.randomUUID().toString(), textures);
		register(set);
		return set;
	}
	
	public TextureSet getByName(String name) {
		return map.get(name);
	}
	
	public boolean isRegistered(String name) {
		return map.containsKey(name);
	}
	
	public Collection<TextureSet> getAllTextureSets() {
		return map.values();
	}
	/** Returns all registered texture sets that are not part of the standard
	 * pack (static constants in {@link TextureSet})/ */
	public Collection<TextureSet> getAllNonStandardTextureSets() {
		List<TextureSet> list = new ArrayList<TextureSet>(map.size());
		for (TextureSet set : map.values()) {
			if (!set.isStandard) {
				list.add(set);
			}
		}
		return list;
	}
}
