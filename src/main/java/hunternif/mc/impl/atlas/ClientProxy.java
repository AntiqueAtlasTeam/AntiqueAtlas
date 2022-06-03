package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.client.TileTextureMap;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements /*SimpleSynchronousResourceReloadListener*/ResourceManagerReloadListener {

	private static ClientProxy instance = null;
	public static ClientProxy instance() {
		if(instance == null) initialize();
		return instance;
	}
	public static void initialize() {
		if(instance == null)
			instance = new ClientProxy();
	}

	private ClientProxy() {}

    /**
     * Assign default textures to vanilla biomes. The textures are assigned
     * only if the biome was not in the config. This prevents unnecessary
     * overwriting, to aid people who manually modify the config.
     */
    private void assignBiomeTextures() {
        // Now let's register every other biome, they'll come from other mods
        for (Biome biome : BuiltinRegistries.BIOME) {
            TileTextureMap.instance().checkRegistration(BuiltinRegistries.BIOME.getKey(biome), biome);
        }
    }

    /**
     * Assign default textures to biomes defined in the client world, but
     * not part of the BuiltinRegistries.BIOME. This happens for all biomes
     * defined in data packs. Also, as these are only available per world,
     * we need the ClientLevel loaded here.
     */
    public static void assignCustomBiomeTextures(ClientLevel world) {
        for (Biome biome : world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)) {
            ResourceLocation id = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
            if (!TileTextureMap.instance().isRegistered(id)) {
                TileTextureMap.instance().autoRegister(id, biome);
            }
        }
    }

//    @Override
//    public ResourceLocation getFabricId() {
//        return AntiqueAtlasMod.id("proxy");
//    }

    @Override
    public void onResourceManagerReload(ResourceManager var1) {
        for (MarkerType type : MarkerType.REGISTRY) {
            type.initMips();
        }
        assignBiomeTextures();
    }
}
