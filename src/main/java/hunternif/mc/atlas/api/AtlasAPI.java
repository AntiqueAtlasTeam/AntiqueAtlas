package hunternif.mc.atlas.api;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.impl.MarkerApiImpl;
import hunternif.mc.atlas.api.impl.TileApiImpl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Use this class to obtain a reference to the APIs.
 * @author Hunternif
 */
public class AtlasAPI {
	private static final int VERSION = 3;
	public static final TileAPI tiles = new TileApiImpl();
	public static final MarkerAPI markers = new MarkerApiImpl();
	
	/** Version of the API, meaning only this particular class. You might
	 * want to check static field VERSION in the specific API interfaces. */
	public static int getVersion() {
		return VERSION;
	}
	
	/** API for biomes and custom tiles (i.e. dungeons, towns etc). */
	public static TileAPI getTileAPI() {
		return tiles;
	}
	
	/** API for custom markers. */
	public static MarkerAPI getMarkerAPI() {
		return markers;
	}
	
	/** Convenience method that returns a list of atlas IDs for all atlas items
	 * the player is currently carrying. **/
	public static List<Integer> getPlayerAtlases(EntityPlayer player) {
		if (!AntiqueAtlasMod.settings.itemNeeded) {
			return Collections.singletonList(player.getUniqueID().hashCode());
		}

		List<Integer> list = new ArrayList<>();
		for (ItemStack stack : player.inventory.mainInventory) {
			if (!stack.isEmpty() && stack.getItem() == AntiqueAtlasMod.itemAtlas) {
				list.add(stack.getItemDamage());
			}
		}

		return list;
	}
}