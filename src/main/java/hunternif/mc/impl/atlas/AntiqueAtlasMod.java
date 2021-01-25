package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.core.AtlasDataHandler;
import hunternif.mc.impl.atlas.core.BiomeDetectorBase;
import hunternif.mc.impl.atlas.core.GlobalAtlasData;
import hunternif.mc.impl.atlas.core.PlayerEventHandler;
import hunternif.mc.impl.atlas.event.RecipeCraftedCallback;
import hunternif.mc.impl.atlas.event.RecipeCraftedHandler;
import hunternif.mc.impl.atlas.ext.TileDataHandler;
import hunternif.mc.impl.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.impl.atlas.marker.MarkersDataHandler;
import hunternif.mc.impl.atlas.mixinhooks.NewPlayerConnectionCallback;
import hunternif.mc.impl.atlas.mixinhooks.NewServerConnectionCallback;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.structure.*;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AntiqueAtlasMod implements ModInitializer {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";

	public static AntiqueAtlasMod instance;
	public static Logger LOG = LogManager.getLogger(NAME);

	public static final AtlasDataHandler atlasData = new AtlasDataHandler();
	public static final MarkersDataHandler markersData = new MarkersDataHandler();

	public static final TileDataHandler tileData = new TileDataHandler();
	public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();

	public static final RecipeCraftedHandler craftedHandler = new RecipeCraftedHandler();

	public static AntiqueAtlasConfig CONFIG = new AntiqueAtlasConfig();

	public static Identifier id(String... path) {
		return path[0].contains(":") ? new Identifier(String.join(".", path)) : new Identifier(ID, String.join(".", path));
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

		BiomeDetectorBase.scanBiomeTypes();

		AutoConfig.register(AntiqueAtlasConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(AntiqueAtlasConfig.class).getConfig();

		RegistrarAntiqueAtlas.register();

		AntiqueAtlasNetworking.registerC2SListeners();

		NewServerConnectionCallback.EVENT.register(atlasData::onClientConnectedToServer);
		NewServerConnectionCallback.EVENT.register(markersData::onClientConnectedToServer);
		NewServerConnectionCallback.EVENT.register(globalMarkersData::onClientConnectedToServer);

		NewPlayerConnectionCallback.EVENT.register(globalMarkersData::onPlayerLogin);
		NewPlayerConnectionCallback.EVENT.register(tileData::onPlayerLogin);
		NewPlayerConnectionCallback.EVENT.register(PlayerEventHandler::onPlayerLogin);

		ServerWorldEvents.LOAD.register(globalMarkersData::onWorldLoad);
		ServerWorldEvents.LOAD.register(tileData::onWorldLoad);

		RecipeCraftedCallback.EVENT.register(craftedHandler);

		StructurePieceAddedCallback.EVENT.register(StructureHandler::resolve);
		StructureAddedCallback.EVENT.register(StructureHandler::resolve);

		NetherFortress.registerPieces();
		EndCity.registerMarkers();
		Village.registerMarkers();
	}
}
