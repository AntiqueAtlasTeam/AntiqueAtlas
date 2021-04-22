package hunternif.mc.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hunternif.mc.api.TileAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.api.impl.MarkerApiImpl;
import hunternif.mc.impl.atlas.api.impl.TileApiImpl;
import hunternif.mc.impl.atlas.item.AtlasItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Use this class to obtain a reference to the APIs.
 * @author Hunternif
 */
public class AtlasAPI {
	private static final int VERSION = 5;
	private static final TileAPI tiles = new TileApiImpl();
	private static final MarkerAPI markers = new MarkerApiImpl();

	/** Version of the API, meaning only this particular class. You might
	 * want to check static field VERSION in the specific API interfaces. */
	public static int getVersion() {
		return VERSION;
	}

	public static Item getAtlasItem() {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation("antiqueatlas:antique_atlas"));
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
	public static List<Integer> getPlayerAtlases(PlayerEntity player) {
		if (!AntiqueAtlasConfig.itemNeeded.get()) {
			return Collections.singletonList(player.getUniqueID().hashCode());
		}

		List<Integer> list = new ArrayList<>();
		for (ItemStack stack : player.inventory.mainInventory) {
			if (!stack.isEmpty() && stack.getItem() instanceof AtlasItem) {
				list.add(AtlasItem.getAtlasID(stack));
			}
		}
		for (ItemStack stack : player.inventory.offHandInventory) {
			if (!stack.isEmpty() && stack.getItem() instanceof AtlasItem) {
				list.add(AtlasItem.getAtlasID(stack));
			}
		}

		return list;
	}
}