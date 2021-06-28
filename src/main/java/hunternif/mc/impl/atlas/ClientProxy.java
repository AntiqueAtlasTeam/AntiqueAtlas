package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.client.*;
import hunternif.mc.impl.atlas.marker.MarkerTextureConfig;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class ClientProxy implements SimpleSynchronousResourceReloadListener
{
    public void initClient()
    {
        // read Textures first from assets
        TextureConfig textureConfig = new TextureConfig(Textures.TILE_TEXTURES_MAP);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(textureConfig);

        // than read TextureSets
        TextureSetMap textureSetMap = TextureSetMap.instance();
        TextureSetConfig textureSetConfig = new TextureSetConfig(textureSetMap);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(textureSetConfig);

        // After that, we can read the tile mappings
        TileTextureMap tileTextureMap = TileTextureMap.instance();
        TileTextureConfig tileTextureConfig = new TileTextureConfig(tileTextureMap, textureSetMap);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(tileTextureConfig);

        // Legacy file name:
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);

        MarkerTextureConfig markerTextureConfig = new MarkerTextureConfig();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(markerTextureConfig);

        for (MarkerType type : MarkerType.REGISTRY)
        {
            type.initMips();
        }

        if (!AntiqueAtlasMod.CONFIG.itemNeeded)
        {
            KeyHandler.registerBindings();
            ClientTickEvents.START_CLIENT_TICK.register(KeyHandler::onClientTick);
        }

    }

    /**
     * Assign default textures to vanilla biomes. The textures are assigned
     * only if the biome was not in the config. This prevents unnecessary
     * overwriting, to aid people who manually modify the config.
     */
    private void assignBiomeTextures()
    {
        // Now let's register every other biome, they'll come from other mods
        for (Biome biome : BuiltinRegistries.BIOME)
        {
            TileTextureMap.instance().checkRegistration(biome);
        }
    }

    @Override
    public Identifier getFabricId()
    {
        return AntiqueAtlasMod.id("proxy");
    }

    @Override
    public void reload(ResourceManager var1)
    {
        for (MarkerType type : MarkerType.REGISTRY)
        {
            type.initMips();
        }
        assignBiomeTextures();
    }
}
