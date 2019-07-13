package hunternif.mc.atlas.registry;

import java.util.List;
import java.util.Set;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.SaveData;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;

public class MarkerRegistry extends SaveData {
	public static final MarkerRegistry INSTANCE = new MarkerRegistry();

	private final MutableRegistry<MarkerType> registry;
	
	private MarkerRegistry() {
		registry = new DefaultedRegistry<>("antiqueatlas:red_x_small");
	}
	
	public static void register(Identifier location, MarkerType type) {
		if (INSTANCE.registry.containsId(location)) {
			int oldId = INSTANCE.registry.getRawId(INSTANCE.registry.get(location));
			INSTANCE.registry.set(oldId, location, type);
		} else {
			INSTANCE.registry.add(location, type);
		}
	}
	
	public static Identifier getLoc(String type) {
		if(!type.contains(":"))
			type = AntiqueAtlasMod.ID + ":" + type;
		return new Identifier(type);
	}
	
	public static MarkerType find(String type) {
		return find(getLoc(type));
	}
	
	public static MarkerType find(Identifier type) {
		return find(type, false);
	}

	public static MarkerType find(Identifier type, boolean fallback) {
		MarkerType mt = INSTANCE.registry.get(type);
		if (mt == null && fallback) {
			return findDefault();
		} else {
			return mt;
		}
	}
	
	public static boolean hasKey(String type) {
		return hasKey(getLoc(type));
	}
	
	public static boolean hasKey(Identifier loc) {
		return INSTANCE.registry.containsId(loc);
	}
	
	public static Iterable<MarkerType> iterable() {
		return INSTANCE.registry;
	}
	
	public static Set<Identifier> getKeys() {
		return INSTANCE.registry.getIds();
	}

	public static Identifier getId(MarkerType type) {
		return INSTANCE.registry.getId(type);
	}

	public static MarkerType findDefault() {
		return find("antiqueatlas:red_x_small");
	}
}
