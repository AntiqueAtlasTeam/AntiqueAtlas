package hunternif.mc.atlas.ext.watcher;

import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;

/**
 * Puts an skull marker to the player's death spot.
 * @author Hunternif
 */
public class DeathWatcher {
	public static void onPlayerDeath(PlayerEntity player) {
		if (SettingsConfig.gameplay.autoDeathMarker) {
			for (int atlasID : AtlasAPI.getPlayerAtlases(player)) {
				AtlasAPI.markers.putMarker(player.getEntityWorld(), true, atlasID, "antiqueatlas:tomb",
										   "gui.antiqueatlas.marker.tomb " + player.getName().getString(),
						(int)player.getX(), (int)player.getZ());
			}
		}
	}
}
