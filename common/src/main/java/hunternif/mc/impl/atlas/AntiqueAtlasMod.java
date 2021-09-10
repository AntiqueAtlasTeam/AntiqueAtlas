package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.core.GlobalAtlasData;
import hunternif.mc.impl.atlas.core.GlobalTileDataHandler;
import hunternif.mc.impl.atlas.core.PlayerEventHandler;
import hunternif.mc.impl.atlas.core.TileDataHandler;
import hunternif.mc.impl.atlas.core.scaning.TileDetectorBase;
import hunternif.mc.impl.atlas.core.scaning.WorldScanner;
import hunternif.mc.impl.atlas.event.RecipeCraftedCallback;
import hunternif.mc.impl.atlas.event.RecipeCraftedHandler;
import hunternif.mc.impl.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.impl.atlas.marker.MarkersDataHandler;
import hunternif.mc.impl.atlas.mixinhooks.NewPlayerConnectionCallback;
import hunternif.mc.impl.atlas.mixinhooks.NewServerConnectionCallback;
import hunternif.mc.impl.atlas.structure.*;
import me.shedaniel.architectury.event.events.LifecycleEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AntiqueAtlasMod implements ModInitializer {
    public static final String ID = "antiqueatlas";
    public static final String NAME = "Antique Atlas";
    public static final WorldScanner worldScanner = new WorldScanner();
    public static final TileDataHandler tileData = new TileDataHandler();
    public static final MarkersDataHandler markersData = new MarkersDataHandler();
    public static final GlobalTileDataHandler globalTileData = new GlobalTileDataHandler();
    public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();
    public static Logger LOG = LogManager.getLogger(NAME);
    public static AntiqueAtlasConfig CONFIG = new AntiqueAtlasConfig();

    public static Identifier id(String... path) {
        return path[0].contains(":") ? new Identifier(String.join(".", path)) : new Identifier(ID, String.join(".", path));
    }

    public static GlobalAtlasData getGlobalAtlasData(World world) {
        if (world.isClient()) {
            LOG.warn("Tried to access server only data from client.");
            return null;
        }

        return ((ServerWorld) world).getPersistentStateManager().getOrCreate(() -> new GlobalAtlasData("antiqueatlas:global_atlas_data"), "antiqueatlas:global_atlas_data");
    }

    @Override
    public void onInitialize() {
        TileDetectorBase.scanBiomeTypes();

        AutoConfig.register(AntiqueAtlasConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AntiqueAtlasConfig.class).getConfig();

        RegistrarAntiqueAtlas.register();

        NewServerConnectionCallback.EVENT.register(tileData::onClientConnectedToServer);
        NewServerConnectionCallback.EVENT.register(markersData::onClientConnectedToServer);
        NewServerConnectionCallback.EVENT.register(globalMarkersData::onClientConnectedToServer);

        NewPlayerConnectionCallback.EVENT.register(globalMarkersData::onPlayerLogin);
        NewPlayerConnectionCallback.EVENT.register(globalTileData::onPlayerLogin);
        NewPlayerConnectionCallback.EVENT.register(PlayerEventHandler::onPlayerLogin);

        LifecycleEvent.SERVER_WORLD_LOAD.register(globalMarkersData::onWorldLoad);
        LifecycleEvent.SERVER_WORLD_LOAD.register(globalTileData::onWorldLoad);

        RecipeCraftedCallback.EVENT.register(new RecipeCraftedHandler());

        StructurePieceAddedCallback.EVENT.register(StructureHandler::resolve);
        StructureAddedCallback.EVENT.register(StructureHandler::resolve);

//        AntiqueAtlasNetworking.registerC2SListeners();

        NetherFortress.registerPieces();
        EndCity.registerMarkers();
        Village.registerMarkers();
        Village.registerPieces();
        Overworld.registerPieces();
    }
}
