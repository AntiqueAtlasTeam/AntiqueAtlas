package hunternif.mc.atlas.ext;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class ExtBiomeDataHandler {
	private static final String DATA_KEY = "aAtlasExtTiles";

	private ExtBiomeData data;

	public void onWorldLoad(ServerWorld world) {
		data = world.getSavedData().getOrCreate(() -> {
			data = new ExtBiomeData(DATA_KEY);
			data.markDirty();
			return data;
		}, DATA_KEY);
	}

	public ExtBiomeData getData() {
		if (data == null) { // This will happen on the client
			data = new ExtBiomeData(DATA_KEY);
		}
		return data;
	}

	public void onPlayerLogin(ServerPlayerEntity player) {
		ExtTileIdMap.instance().syncOnPlayer(player);
		data.syncOnPlayer(player);
	}

}
