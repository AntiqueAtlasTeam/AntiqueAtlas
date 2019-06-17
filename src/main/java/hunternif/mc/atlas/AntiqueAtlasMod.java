package hunternif.mc.atlas;

import hunternif.mc.atlas.core.AtlasDataHandler;
import hunternif.mc.atlas.core.GlobalAtlasData;
import hunternif.mc.atlas.core.PlayerEventHandler;
import hunternif.mc.atlas.event.RecipeCraftedCallback;
import hunternif.mc.atlas.event.RecipeCraftedHandler;
import hunternif.mc.atlas.ext.*;
import hunternif.mc.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.atlas.marker.MarkersDataHandler;
import hunternif.mc.atlas.mixinhooks.NewPlayerConnectionCallback;
import hunternif.mc.atlas.mixinhooks.NewServerConnectionCallback;
import hunternif.mc.atlas.mixinhooks.ServerWorldLoadCallback;
import hunternif.mc.atlas.network.PacketDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AntiqueAtlasMod implements ModInitializer {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";
	public static final String CHANNEL = ID;
	public static final String VERSION = "@VERSION@";

	public static AntiqueAtlasMod instance;
	public static Logger logger = LogManager.getLogger();

	public static CommonProxy proxy;

	public static final AtlasDataHandler atlasData = new AtlasDataHandler();
	public static final MarkersDataHandler markersData = new MarkersDataHandler();

	public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
	public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();

	public static final RecipeCraftedHandler craftedHandler = new RecipeCraftedHandler();

	public static Identifier id(String name) {
		if (name.indexOf(':') > 0) {
			return new Identifier(name);
		} else {
			return new Identifier("antiqueatlas", name);
		}
	}

	// TODO FABRIC cleanup
	private static final GlobalAtlasData clientAtlasData = new GlobalAtlasData("antiqueatlas:global_atlas_data");

	public static GlobalAtlasData getGlobalAtlasData(World world) {
		if (world instanceof ServerWorld) {
			return ((ServerWorld) world).getPersistentStateManager().getOrCreate(() -> new GlobalAtlasData("antiqueatlas:global_atlas_data"), "antiqueatlas:global_atlas_data");
		} else {
			return clientAtlasData;
		}
	}

	@Override
	public void onInitialize() {
		instance = this;
		proxy = new CommonProxy();

		RegistrarAntiqueAtlas.register();
		PacketDispatcher.registerPackets();

		proxy.init();

		NewServerConnectionCallback.EVENT.register(atlasData::onClientConnectedToServer);
		NewServerConnectionCallback.EVENT.register(markersData::onClientConnectedToServer);
		NewServerConnectionCallback.EVENT.register(globalMarkersData::onClientConnectedToServer);

		NewPlayerConnectionCallback.EVENT.register(globalMarkersData::onPlayerLogin);
		NewPlayerConnectionCallback.EVENT.register(extBiomeData::onPlayerLogin);
		NewPlayerConnectionCallback.EVENT.register(PlayerEventHandler::onPlayerLogin);

		ServerWorldLoadCallback.EVENT.register(globalMarkersData::onWorldLoad);
		ServerWorldLoadCallback.EVENT.register(extBiomeData::onWorldLoad);

		RecipeCraftedCallback.EVENT.register(craftedHandler::onCrafted);

		/*
		if (!SettingsConfig.gameplay.itemNeeded)
            MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

		MinecraftForge.EVENT_BUS.register(extBiomeData);

		MinecraftForge.EVENT_BUS.register(new DeathWatcher());

		MinecraftForge.EVENT_BUS.register(new NetherPortalWatcher());

		// Structure Watchers
        new StructureWatcherVillage();
        new StructureWatcherFortress();
		new StructureWatcherGeneric("EndCity", XX_1_13_none_bnu_XX.c, MarkerTypes.END_CITY_FAR, "gui.antiqueatlas.marker.endcity").setTileMarker(MarkerTypes.END_CITY, "gui.antiqueatlas.marker.endcity");

		 */
	}
}
