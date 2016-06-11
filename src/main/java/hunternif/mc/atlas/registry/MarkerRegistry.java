package hunternif.mc.atlas.registry;

import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;

import net.minecraft.util.ResourceLocation;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.Log;

public enum MarkerRegistry {
	INSTANCE;
	
	private final ResourceLocation markerRegistryLocation = new ResourceLocation(AntiqueAtlasMod.ID, "markers");
	private final ResourceLocation DEFAULT_LOC = new ResourceLocation(AntiqueAtlasMod.ID, "red_x_small");
	private final int MIN_ID = 0, MAX_ID = 255;
	
	private final FMLControlledNamespacedRegistry<MarkerType> registry;
	
	private MarkerRegistry() {
		registry = PersistentRegistryManager.createRegistry(markerRegistryLocation, MarkerType.class, DEFAULT_LOC, MIN_ID, MAX_ID, true, Callback.INSTANCE, Callback.INSTANCE, Callback.INSTANCE);
	}
	
	public static void register(ResourceLocation location, MarkerType type) {
		type.setRegistryName(location);
		register(type);
	}
	
	public static void register(MarkerType type) {
		INSTANCE.registry.register(type);
	}
	
	public static MarkerType find(String type) {
		if(!type.contains(":"))
			type = AntiqueAtlasMod.ID + ":" + type;
		return INSTANCE.registry.getObject(new ResourceLocation(type));
	}
	
	public static List<MarkerType> getValues() {
		return INSTANCE.registry.getValues();
	}
	
	private static enum Callback implements IForgeRegistry.AddCallback<MarkerType>,IForgeRegistry.ClearCallback<MarkerType>,IForgeRegistry.CreateCallback<MarkerType> {
		INSTANCE;

		@Override
		public void onCreate(Map<ResourceLocation, ?> slaveset) {
		}

		@Override
		public void onClear(Map<ResourceLocation, ?> slaveset) {
		}

		@Override
		public void onAdd(MarkerType obj, int id, Map<ResourceLocation, ?> slaveset) {
		}
		
	}
	
}
