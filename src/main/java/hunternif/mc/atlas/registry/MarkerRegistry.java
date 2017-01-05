package hunternif.mc.atlas.registry;

import java.util.List;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.SaveData;

public class MarkerRegistry extends SaveData {
	public static final MarkerRegistry INSTANCE = new MarkerRegistry();

	private static final ResourceLocation DEFAULT_LOC = new ResourceLocation("antiqueatlas:red_x_small");
	
	private final MarkerRegistryImpl<MarkerType> registry;
	
	private MarkerRegistry() {
		registry = new MarkerRegistryImpl<>(DEFAULT_LOC);
	}
	
	public static void register(ResourceLocation location, MarkerType type) {
		type.setRegistryName(location);
		register(type);
	}
	
	public static void register(MarkerType type) {
		INSTANCE.registry.register(type);
		INSTANCE.markDirty();
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
		return INSTANCE.registry.getObject(type);
	}
	
	public static boolean hasKey(String type) {
		return hasKey(getLoc(type));
	}
	
	public static boolean hasKey(ResourceLocation loc) {
		return INSTANCE.registry.containsKey(loc);
	}
	
	public static List<MarkerType> getValues() {
		return INSTANCE.registry.getValues();
	}
	
	public static Set<ResourceLocation> getKeys() {
		return INSTANCE.registry.getKeys();
	}
}
