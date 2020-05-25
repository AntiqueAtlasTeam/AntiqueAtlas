package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.SaveData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

/**
 * Maps texture sets to their names.
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class TextureSetMap extends SaveData {
	private static final TextureSetMap INSTANCE = new TextureSetMap();
	public static TextureSetMap instance() {
		return INSTANCE;
	}
	
	private final Map<ResourceLocation, TextureSet> map = new HashMap<>();
	
	public void register(TextureSet set) {
		TextureSet old = map.put(set.name, set);
		// If the old texture set is equal to the new one (i.e. has equal name
		// and equal texture files), then there's no need to update the config.
		if (!set.equals(old)) {
			markDirty();
		}
	}

	public TextureSet getByName(ResourceLocation name) {
		return map.get(name);
	}
	
	/** If the specified name is not registered, returns a "TEST" texture set. */
	public TextureSet getByNameNonNull(ResourceLocation name) {
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
