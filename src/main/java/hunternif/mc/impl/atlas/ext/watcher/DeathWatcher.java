package hunternif.mc.impl.atlas.ext.watcher;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

/**
 * Puts an skull marker to the player's death spot.
 * @author Hunternif, Haven King
 */
public class DeathWatcher {
	public static void onPlayerDeath(PlayerEntity player) {
		if (AntiqueAtlasMod.CONFIG.autoDeathMarker) {
			for (int atlasID : AtlasAPI.getPlayerAtlases(player)) {
				AtlasAPI.markers.putMarker(player.getEntityWorld(), true, atlasID, MarkerType.REGISTRY.get(new Identifier("antiqueatlas:tomb")),
						new TranslatableText("gui.antiqueatlas.marker.tomb", player.getName()),
						(int)player.getX(), (int)player.getZ());
			}
		}
	}
}
