package hunternif.mc.atlas.registry;

import java.util.List;
import java.util.Set;

import com.mojang.serialization.Lifecycle;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.SaveData;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class MarkerRegistry extends SaveData {
	public static final MarkerRegistry INSTANCE = new MarkerRegistry();
	public static final Identifier MARKER_REGISTRY = new Identifier(AntiqueAtlasMod.ID, "marker");
	public static final RegistryKey<Registry<MarkerType>> MARKER_TYPE_REGISTRY_KEY =
			RegistryKey.ofRegistry(MARKER_REGISTRY);

	private final MutableRegistry<MarkerType> registry;


	private MarkerRegistry() {
		registry = new DefaultedRegistry<>("antiqueatlas:red_x_small", MARKER_TYPE_REGISTRY_KEY,
										   Lifecycle.stable());
	}
	
	public static void register(Identifier location, MarkerType type) {
		if (INSTANCE.registry.containsId(location)) {
			int oldId = INSTANCE.registry.getRawId(INSTANCE.registry.get(location));
			INSTANCE.registry.set(oldId, RegistryKey.of(MARKER_TYPE_REGISTRY_KEY, location), type);
		} else {
			INSTANCE.registry.add(RegistryKey.of(MARKER_TYPE_REGISTRY_KEY, location), type);
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
