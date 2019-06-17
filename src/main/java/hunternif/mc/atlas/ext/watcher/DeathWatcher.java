package hunternif.mc.atlas.ext.watcher;

import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * Puts an skull marker to the player's death spot.
 * @author Hunternif
 */
public class DeathWatcher {
	public static void onPlayerDeath(PlayerEntity player) {
		if (SettingsConfig.gameplay.autoDeathMarker) {
			for (int atlasID : AtlasAPI.getPlayerAtlases(player)) {
				AtlasAPI.markers.putMarker(player.getEntityWorld(), true, atlasID, "antiqueatlas:tomb",
						new TranslatableComponent("gui.antiqueatlas.marker.tomb").append(player.getName()).getString(),
						(int)player.x, (int)player.z);
			}
		}
	}
}
