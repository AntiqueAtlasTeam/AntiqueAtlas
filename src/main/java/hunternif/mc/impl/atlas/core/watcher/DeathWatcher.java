package hunternif.mc.impl.atlas.core.watcher;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.api.AtlasAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Puts an skull marker to the player's death spot.
 * @author Hunternif, Haven King
 */
public class DeathWatcher {
	public static void onPlayerDeath(PlayerEntity player) {
		if (AntiqueAtlasConfig.autoDeathMarker.get()) {
			for (int atlasID : AtlasAPI.getPlayerAtlases(player)) {
				AtlasAPI.getMarkerAPI().putMarker(player.getEntityWorld(), true, atlasID, new ResourceLocation("antiqueatlas:tomb"),
                        new TranslationTextComponent("gui.antiqueatlas.marker.tomb", player.getName()),
                        (int) player.getPosX(), (int) player.getPosZ());
			}
		}
	}
}
