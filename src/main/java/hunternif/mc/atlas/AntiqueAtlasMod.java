package hunternif.mc.atlas;

import hunternif.mc.atlas.core.AtlasDataHandler;
import hunternif.mc.atlas.core.GlobalAtlasData;
import hunternif.mc.atlas.core.PlayerEventHandler;
import hunternif.mc.atlas.ext.*;
import hunternif.mc.atlas.ext.watcher.*;
import hunternif.mc.atlas.ext.watcher.impl.StructureWatcherFortress;
import hunternif.mc.atlas.ext.watcher.impl.StructureWatcherGeneric;
import hunternif.mc.atlas.ext.watcher.impl.StructureWatcherVillage;
import hunternif.mc.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.atlas.marker.MarkersDataHandler;
import hunternif.mc.atlas.marker.NetherPortalWatcher;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.registry.MarkerTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class AntiqueAtlasMod implements ModInitializer {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";
	public static final String CHANNEL = ID;
	public static final String VERSION = "@VERSION@";

	public static AntiqueAtlasMod instance;

	public static CommonProxy proxy;

	public static final AtlasDataHandler atlasData = new AtlasDataHandler();
	public static final MarkersDataHandler markersData = new MarkersDataHandler();

	public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
	public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();

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

		/*
		if (!SettingsConfig.gameplay.itemNeeded)
            MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

		MinecraftForge.EVENT_BUS.register(atlasData);
		MinecraftForge.EVENT_BUS.register(markersData);

		MinecraftForge.EVENT_BUS.register(extBiomeData);

		MinecraftForge.EVENT_BUS.register(globalMarkersData);

		MinecraftForge.EVENT_BUS.register(new DeathWatcher());

		MinecraftForge.EVENT_BUS.register(new NetherPortalWatcher());

		// Structure Watchers
        new StructureWatcherVillage();
        new StructureWatcherFortress();
		new StructureWatcherGeneric("EndCity", XX_1_13_none_bnu_XX.c, MarkerTypes.END_CITY_FAR, "gui.antiqueatlas.marker.endcity").setTileMarker(MarkerTypes.END_CITY, "gui.antiqueatlas.marker.endcity");

		 */
	}
}
