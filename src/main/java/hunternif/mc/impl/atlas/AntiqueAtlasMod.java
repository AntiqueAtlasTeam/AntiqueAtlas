package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.core.TileDataHandler;
import hunternif.mc.impl.atlas.core.scaning.TileDetectorBase;
import hunternif.mc.impl.atlas.core.GlobalAtlasData;
import hunternif.mc.impl.atlas.core.scaning.WorldScanner;
import hunternif.mc.impl.atlas.event.RecipeCraftedCallback;
import hunternif.mc.impl.atlas.event.RecipeCraftedHandler;
import hunternif.mc.impl.atlas.forge.event.ServerWorldEvents;
import hunternif.mc.impl.atlas.core.GlobalTileDataHandler;
import hunternif.mc.impl.atlas.core.PlayerEventHandler;
import hunternif.mc.impl.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.impl.atlas.marker.MarkersDataHandler;
import hunternif.mc.impl.atlas.mixinhooks.NewPlayerConnectionCallback;
import hunternif.mc.impl.atlas.mixinhooks.NewServerConnectionCallback;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.structure.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AntiqueAtlasMod.ID)
public class AntiqueAtlasMod {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";

	public static Logger LOG = LogManager.getLogger(NAME);

	public static final WorldScanner worldScanner = new WorldScanner();
	public static final TileDataHandler tileData = new TileDataHandler();
	public static final MarkersDataHandler markersData = new MarkersDataHandler();

	public static final GlobalTileDataHandler globalTileData = new GlobalTileDataHandler();
	public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();

	public static AntiqueAtlasConfig CONFIG = new AntiqueAtlasConfig();

	public static ResourceLocation id(String... path) {
		return path[0].contains(":") ? new ResourceLocation(String.join(".", path)) : new ResourceLocation(ID, String.join(".", path));
	}

	public static GlobalAtlasData getGlobalAtlasData(Level world) {
		if (world.isClientSide()) {
			LOG.warn("Tried to access server only data from client.");
			return null;
		}

		return ((ServerLevel) world).getDataStorage().computeIfAbsent(GlobalAtlasData::readNbt, GlobalAtlasData::new, "antiqueatlas:global_atlas_data");
	}

	public void onInitialize() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<FMLCommonSetupEvent>)common-> {
			TileDetectorBase.scanBiomeTypes();
		});

		AutoConfig.register(AntiqueAtlasConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(AntiqueAtlasConfig.class).getConfig();

		RegistrarAntiqueAtlas.register();

		AntiqueAtlasNetworking.registerC2SListeners();

		NewServerConnectionCallback.register(tileData::onClientConnectedToServer);
		NewServerConnectionCallback.register(markersData::onClientConnectedToServer);
		NewServerConnectionCallback.register(globalMarkersData::onClientConnectedToServer);

		NewPlayerConnectionCallback.register(globalMarkersData::onPlayerLogin);
		NewPlayerConnectionCallback.register(globalTileData::onPlayerLogin);
		NewPlayerConnectionCallback.register(PlayerEventHandler::onPlayerLogin);

		ServerWorldEvents.register(globalMarkersData::onWorldLoad);
		ServerWorldEvents.register(globalTileData::onWorldLoad);

		RecipeCraftedCallback.register(new RecipeCraftedHandler());

		StructurePieceAddedCallback.register(StructureHandler::resolve);
		StructureAddedCallback.register(StructureHandler::resolve);

		FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<FMLCommonSetupEvent>)common-> {
			NetherFortress.registerPieces();
			EndCity.registerMarkers();
			Village.registerMarkers();
			Village.registerPieces();
			Overworld.registerPieces();
		});
	}

	////FORGE ONLY
	public static final SimpleChannel MOD_CHANNEL = NetworkRegistry.newSimpleChannel(id("main"), () -> "1", "1"::equals, "1"::equals);
	public AntiqueAtlasMod()
	{
		this.onInitialize();

		AntiqueAtlasNetworking.registerS2CListeners();

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new AntiqueAtlasModClient()::onInitializeClient);
	}
}