package hunternif.mc.atlas.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.impl.MarkerApiImpl;
import hunternif.mc.atlas.api.impl.TileApiImpl;

/**
 * Use this class to obtain a reference to the APIs.
 * @author Hunternif
 */
public class AtlasAPI {
	private static final int VERSION = 3;
	private static final TileAPI tileApi = new TileApiImpl();
	private static final MarkerAPI markerApi = new MarkerApiImpl();
	
	/** Version of the API, meaning only this particular class. You might
	 * want to check static field VERSION in the specific API interfaces. */
	public static int getVersion() {
		return VERSION;
	}
	
	/** API for biomes and custom tiles (i.e. dungeons, towns etc). */
	public static TileAPI getTileAPI() {
		return tileApi;
	}
	
	/** API for custom markers. */
	public static MarkerAPI getMarkerAPI() {
		return markerApi;
	}
	
	/** Convenience method that returns a list of atlas IDs for all atlas items
	 * the player is currently carrying. **/
	public static List<Integer> getPlayerAtlases(EntityPlayer player) {
		List<Integer> list = new ArrayList<Integer>();
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack != null && stack.getItem() == AntiqueAtlasMod.itemAtlas) {
				list.add(stack.getItemDamage());
			}
		}
		return list;
	}
}
