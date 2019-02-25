package hunternif.mc.atlas.ext.watcher;

/**
 * Puts an skull marker to the player's death spot.
 * @author Hunternif
 */
public class DeathWatcher {
	/* @SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof PlayerEntity && SettingsConfig.gameplay.autoDeathMarker) {
			PlayerEntity player = (PlayerEntity) event.getEntity();
			for (int atlasID : AtlasAPI.getPlayerAtlases(player)) {
				AtlasAPI.markers.putMarker(player.getEntityWorld(), true, atlasID, MarkerRegistry.getId(MarkerTypes.TOMB).toString(),
						"gui.antiqueatlas.marker.tomb " + player.XX_1_12_2_h__XX(),
						(int)player.x, (int)player.z);
			}
		}
	} */
	// TODO FABRIC
}
