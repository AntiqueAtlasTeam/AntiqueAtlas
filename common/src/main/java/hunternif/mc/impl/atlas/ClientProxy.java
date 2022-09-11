package hunternif.mc.impl.atlas;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import hunternif.mc.impl.atlas.client.*;
import hunternif.mc.impl.atlas.marker.MarkerTextureConfig;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class ClientProxy implements ResourceReloader {
    public void initClient() {
        // read Textures first from assets
        TextureConfig textureConfig = new TextureConfig(Textures.TILE_TEXTURES_MAP);
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, textureConfig);

        // then read TextureSets
        TextureSetMap textureSetMap = TextureSetMap.instance();
        TextureSetConfig textureSetConfig = new TextureSetConfig(textureSetMap);
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, textureSetConfig);

        // After that, we can read the tile mappings
        TileTextureMap tileTextureMap = TileTextureMap.instance();
        TileTextureConfig tileTextureConfig = new TileTextureConfig(tileTextureMap, textureSetMap);
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, tileTextureConfig);

        // Legacy file name:
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, this);

        MarkerTextureConfig markerTextureConfig = new MarkerTextureConfig();
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, markerTextureConfig);

        for (MarkerType type : MarkerType.REGISTRY) {
            type.initMips();
        }

        if (!AntiqueAtlasMod.CONFIG.itemNeeded) {
            KeyHandler.registerBindings();
            ClientTickEvent.CLIENT_POST.register(KeyHandler::onClientTick);
        }

    }

    /**
     * Assign default textures to vanilla biomes. The textures are assigned
     * only if the biome was not in the config. This prevents unnecessary
     * overwriting, to aid people who manually modify the config.
     */
    private void assignBiomeTextures() {
        // Now let's register every other biome, they'll come from other mods
        for (Biome biome : BuiltinRegistries.BIOME) {
            TileTextureMap.instance().checkRegistration(BuiltinRegistries.BIOME.getId(biome), biome);
        }
    }

    /**
     * Assign default textures to biomes defined in the client world, but
     * not part of the BuiltinRegistries.BIOME. This happens for all biomes
     * defined in data packs. Also, as these are only available per world,
     * we need the ClientWorld loaded here.
     */
    public static void assignCustomBiomeTextures(ClientWorld world) {
        for (Biome biome : world.getRegistryManager().get(Registry.BIOME_KEY)) {
            Identifier id = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
            if (!TileTextureMap.instance().isRegistered(id)) {
                TileTextureMap.instance().autoRegister(id, biome);
            }
        }
    }

    @Override
    public String getName() {
        return AntiqueAtlasMod.id("proxy").toString();
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.completedFuture(null).thenCompose(synchronizer::whenPrepared).thenCompose(t -> CompletableFuture.runAsync(() -> {
            for (MarkerType type : MarkerType.REGISTRY) {
                type.initMips();
            }
            assignBiomeTextures();
        }));
    }
}
