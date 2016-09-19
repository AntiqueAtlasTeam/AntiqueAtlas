package hunternif.mc.atlas.registry;

import net.minecraft.util.ResourceLocation;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.ext.VillageWatcher;
import hunternif.mc.atlas.marker.NetherPortalWatcher;

public class MarkerTypes {
	
	public static MarkerTypes INSTANCE = new MarkerTypes();
	
	public static MarkerType
	
	GOOGLE,
	RED_X_LARGE,
	RED_X_SMALL,
	VILLAGE,
	DIAMOND,
	BED,
	PICKAXE,
	SWORD,
	NETHER_PORTAL,
	SKULL,
	TOWER,
	SCROLL,
	TOMB,
	END_CITY,
	END_CITY_FAR;
	
	private MarkerTypes() {
		GOOGLE = marker("google", Textures.MARKER_GOOGLE_MARKER);
		RED_X_LARGE = marker("red_x_large", Textures.MARKER_RED_X_LARGE);
		RED_X_SMALL = marker("red_x_small", Textures.MARKER_RED_X_SMALL);
		DIAMOND = marker("diamond", Textures.MARKER_DIAMOND);
		BED = marker("bed", Textures.MARKER_BED);
		PICKAXE = marker("pickaxe", Textures.MARKER_PICKAXE);
		SWORD = marker("sword", Textures.MARKER_SWORD);
		SKULL = marker("skull", Textures.MARKER_SKULL);
		TOWER = marker("tower", Textures.MARKER_TOWER);
		SCROLL = marker("scroll", Textures.MARKER_SCROLL);
		TOMB = marker("tomb", Textures.MARKER_TOMB);
		
		VILLAGE = marker(VillageWatcher.MARKER, Textures.MARKER_VILLAGE);
		NETHER_PORTAL = marker(NetherPortalWatcher.MARKER_PORTAL, Textures.MARKER_NETHER_PORTAL);

		END_CITY = marker("EndCity", Textures.MARKER_END_CITY, Textures.MARKER_END_CITY_MIP_32, Textures.MARKER_END_CITY_MIP_16);
		END_CITY.setAlwaysShow(true).setClip(-1, 1000).setIsTile(true).setSize(2).setCenter(0.5, 0.75).setIsTechnical(true);
		END_CITY_FAR = marker("EndCity_Far", Textures.MARKER_END_CITY_FAR);
		END_CITY_FAR.setSize(1).setClip(-1000, -2).setIsTechnical(true);
		// = marker("", Textures.MARKER_);
	}
	
	private MarkerType marker(String name, ResourceLocation... textures) {
		MarkerType type = new MarkerType(new ResourceLocation(AntiqueAtlasMod.ID, name), textures);
		reg(type);
		return type;
	}
	
	private void reg(MarkerType type) {
		MarkerRegistry.register(type);
	}

}
