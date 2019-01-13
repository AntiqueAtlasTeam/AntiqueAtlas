package hunternif.mc.atlas;

import hunternif.mc.atlas.core.AtlasDataHandler;
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
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=AntiqueAtlasMod.ID, name=AntiqueAtlasMod.NAME, version=AntiqueAtlasMod.VERSION, dependencies="after:forge@[14.23.2.2611,);")
public class AntiqueAtlasMod {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";
	public static final String CHANNEL = ID;
	public static final String VERSION = "@VERSION@";

	@Instance(ID)
	public static AntiqueAtlasMod instance;

	public boolean jeidPresent = false;

	@SidedProxy(clientSide="hunternif.mc.atlas.ClientProxy", serverSide="hunternif.mc.atlas.CommonProxy")
	public static CommonProxy proxy;

	public static final AtlasDataHandler atlasData = new AtlasDataHandler();
	public static final MarkersDataHandler markersData = new MarkersDataHandler();

	public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
	public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		PacketDispatcher.registerPackets();
		proxy.init(event);

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
		new StructureWatcherGeneric("EndCity", DimensionType.THE_END, MarkerTypes.END_CITY_FAR, "gui.antiqueatlas.marker.endcity").setTileMarker(MarkerTypes.END_CITY, "gui.antiqueatlas.marker.endcity");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
