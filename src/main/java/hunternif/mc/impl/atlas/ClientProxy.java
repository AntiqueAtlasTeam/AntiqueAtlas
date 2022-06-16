package hunternif.mc.impl.atlas;

import java.util.function.Consumer;

import hunternif.mc.impl.atlas.client.*;
import hunternif.mc.impl.atlas.marker.MarkerTextureConfig;
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
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements /*SimpleSynchronousResourceReloadListener*/ResourceManagerReloadListener {
    public void initClient() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((Consumer<RegisterClientReloadListenersEvent>)resourceManager-> {
            // read Textures first from assets
            TextureConfig textureConfig = new TextureConfig(Textures.TILE_TEXTURES_MAP);
            resourceManager
                    .registerReloadListener(textureConfig);

            // than read TextureSets
            TextureSetMap textureSetMap = TextureSetMap.instance();
            TextureSetConfig textureSetConfig = new TextureSetConfig(textureSetMap);
            resourceManager
                    .registerReloadListener(textureSetConfig);

            // After that, we can read the tile mappings
            TileTextureMap tileTextureMap = TileTextureMap.instance();
            TileTextureConfig tileTextureConfig = new TileTextureConfig(tileTextureMap, textureSetMap);
            resourceManager
                    .registerReloadListener(tileTextureConfig);

            // Legacy file name:
            resourceManager
                    .registerReloadListener(this);

            MarkerTextureConfig markerTextureConfig = new MarkerTextureConfig();
            resourceManager
                    .registerReloadListener(markerTextureConfig);
        });
        modEventBus.addListener((Consumer<FMLClientSetupEvent>)resourceManager-> {
            for (MarkerType type : MarkerType.REGISTRY) {
                type.initMips();
            }

            if (!AntiqueAtlasMod.CONFIG.itemNeeded) {
                KeyHandler.registerBindings();
                MinecraftForge.EVENT_BUS.addListener(KeyHandler::onClientTick);
            }
        });
    }

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