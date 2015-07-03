package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.SaveData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Maps texture sets to their names.
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class TextureSetMap extends SaveData {
	private static final TextureSetMap INSTANCE = new TextureSetMap();
	public static final TextureSetMap instance() {
		return INSTANCE;
	}
	
	private final Map<String, TextureSet> map = new HashMap<String, TextureSet>();
	
	public void register(TextureSet set) {
		TextureSet old = map.put(set.name, set);
		// If the old texture set is equal to the new one (i.e. has equal name
		// and equal texture files), then there's no need to update the config.
		if (!set.equals(old)) {
			markDirty();
		}
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
	
	/** If the specified name is not registered, returns a "TEST" texture set. */
	public TextureSet getByNameNonNull(String name) {
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
