package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.util.SaveData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps texture sets to their names.
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class TextureSetMap extends SaveData {
	private static final TextureSetMap INSTANCE = new TextureSetMap();
	public static TextureSetMap instance() {
		return INSTANCE;
	}
	
	private final Map<Identifier, TextureSet> map = new HashMap<>();
	
	public void register(TextureSet set) {
		TextureSet old = map.put(set.name, set);
		// If the old texture set is equal to the new one (i.e. has equal name
		// and equal texture files), then there's no need to update the config.
		if (!set.equals(old)) {
			markDirty();
		}
	}

	public TextureSet getByName(Identifier name) {
		return map.get(name);
	}
	
	/** If the specified name is not registered, returns a "TEST" texture set. */
	public TextureSet getByNameNonNull(Identifier name) {
		TextureSet set = getByName(name);
		return set == null ? TextureSet.TEST : set;
	}
	
	public boolean isRegistered(String name) {
		return map.containsKey(name);
	}
	
	public Collection<TextureSet> getAllTextureSets() {
		return map.values();
	}
	/** Returns all registered texture sets that are not part of the standard
	 * pack (static constants in {@link TextureSet})/ */
	Collection<TextureSet> getAllNonStandardTextureSets() {
		List<TextureSet> list = new ArrayList<>(map.size());
		for (TextureSet set : map.values()) {
			if (!set.isStandard) {
				list.add(set);
			}
		}
		return list;
	}
}
