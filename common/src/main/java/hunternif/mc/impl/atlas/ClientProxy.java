package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.client.*;
import hunternif.mc.impl.atlas.marker.MarkerTextureConfig;
import hunternif.mc.impl.atlas.registry.MarkerType;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.architectury.registry.ReloadListeners;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class ClientProxy implements ResourceReloadListener {
    public void initClient() {
        // read Textures first from assets
        TextureConfig textureConfig = new TextureConfig(Textures.TILE_TEXTURES_MAP);
        ReloadListeners.registerReloadListener(ResourceType.CLIENT_RESOURCES, textureConfig);

        // than read TextureSets
        TextureSetMap textureSetMap = TextureSetMap.instance();
        TextureSetConfig textureSetConfig = new TextureSetConfig(textureSetMap);
        ReloadListeners.registerReloadListener(ResourceType.CLIENT_RESOURCES, textureSetConfig);

        // After that, we can read the tile mappings
        TileTextureMap tileTextureMap = TileTextureMap.instance();
        TileTextureConfig tileTextureConfig = new TileTextureConfig(tileTextureMap, textureSetMap);
        ReloadListeners.registerReloadListener(ResourceType.CLIENT_RESOURCES, tileTextureConfig);

        ReloadListeners.registerReloadListener(ResourceType.CLIENT_RESOURCES, this);

        MarkerTextureConfig markerTextureConfig = new MarkerTextureConfig();
        ReloadListeners.registerReloadListener(ResourceType.CLIENT_RESOURCES, markerTextureConfig);

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
            TileTextureMap.instance().checkRegistration(biome);
        }
    }

    @Override
    public String getName() {
        return "antiqueatlas:proxy";
    }


    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.completedFuture(null).thenCompose(synchronizer::whenPrepared).thenCompose(t -> CompletableFuture.runAsync(() -> {
            for (MarkerType type : MarkerType.REGISTRY) {
                type.initMips();
            }
            assignBiomeTextures();
        }));
    }
}
