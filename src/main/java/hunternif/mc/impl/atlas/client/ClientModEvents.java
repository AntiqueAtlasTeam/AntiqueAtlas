package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.ClientProxy;
import hunternif.mc.impl.atlas.marker.MarkerTextureConfig;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AntiqueAtlasMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent resourceManager) {
        // read Textures first from assets
        TextureConfig textureConfig = new TextureConfig(Textures.TILE_TEXTURES_MAP);
        resourceManager
                .registerReloadListener(textureConfig);

        // then read TextureSets
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
                .registerReloadListener(ClientProxy.instance());

        MarkerTextureConfig markerTextureConfig = new MarkerTextureConfig();
        resourceManager
                .registerReloadListener(markerTextureConfig);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        for (MarkerType type : MarkerType.REGISTRY) {
            type.initMips();
        }

        if (!AntiqueAtlasMod.CONFIG.itemNeeded) {
            KeyHandler.registerBindings();
            MinecraftForge.EVENT_BUS.addListener(KeyHandler::onClientTick);
        }
    }

}
