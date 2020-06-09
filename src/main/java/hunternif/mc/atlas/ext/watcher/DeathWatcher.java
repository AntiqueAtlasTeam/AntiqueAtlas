package hunternif.mc.atlas.ext.watcher;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;

/**
 * Puts an skull marker to the player's death spot.
 * @author Hunternif
 */
public class DeathWatcher {
	public static void onPlayerDeath(PlayerEntity player) {
		if (AntiqueAtlasMod.CONFIG.gameplay.autoDeathMarker) {
			for (int atlasID : AtlasAPI.getPlayerAtlases(player)) {
				AtlasAPI.markers.putMarker(player.getEntityWorld(), true, atlasID, "antiqueatlas:tomb",
						new TranslatableText("gui.antiqueatlas.marker.tomb").append(player.getName()).getString(),
						(int)player.getX(), (int)player.getZ());
			}
		}
	}
}
