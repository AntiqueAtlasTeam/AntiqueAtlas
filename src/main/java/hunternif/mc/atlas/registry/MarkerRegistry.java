package hunternif.mc.atlas.registry;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.SaveData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;

import java.util.Set;

public class MarkerRegistry extends SaveData {
	public static final MarkerRegistry INSTANCE = new MarkerRegistry();

	private final MutableRegistry<MarkerType> registry;
	
	private MarkerRegistry() {
		registry = new DefaultedRegistry<>("antiqueatlas:red_x_small");
	}
	
	public static void register(ResourceLocation location, MarkerType type) {
		if (INSTANCE.registry.containsKey(location)) {
			int oldId = INSTANCE.registry.getId(INSTANCE.registry.getOrDefault(location));
			INSTANCE.registry.register(oldId, location, type);
		} else {
			INSTANCE.registry.register(location, type);
		}
	}
	
	public static ResourceLocation getLoc(String type) {
		if(!type.contains(":"))
			type = AntiqueAtlasMod.ID + ":" + type;
		return new ResourceLocation(type);
	}
	
	public static MarkerType find(String type) {
		return find(getLoc(type));
	}
	
	public static MarkerType find(ResourceLocation type) {
		return find(type, false);
	}

	public static MarkerType find(ResourceLocation type, boolean fallback) {
		MarkerType mt = INSTANCE.registry.getOrDefault(type);
		if (mt == null && fallback) {
			return findDefault();
		} else {
			return mt;
		}
	}
	
	public static boolean hasKey(String type) {
		return hasKey(getLoc(type));
	}
	
	public static boolean hasKey(ResourceLocation loc) {
		return INSTANCE.registry.containsKey(loc);
	}
	
	public static Iterable<MarkerType> iterable() {
		return INSTANCE.registry;
	}
	
	public static Set<ResourceLocation> getKeys() {
		return INSTANCE.registry.keySet();
	}

	public static ResourceLocation getId(MarkerType type) {
		return INSTANCE.registry.getKey(type);
	}

	public static MarkerType findDefault() {
		return find("antiqueatlas:red_x_small");
	}
}
