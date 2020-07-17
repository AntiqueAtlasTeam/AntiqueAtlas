package hunternif.mc.impl.atlas.ext;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import hunternif.mc.impl.atlas.network.packet.s2c.play.TileNameS2CPacket;
import hunternif.mc.impl.atlas.util.SaveData;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


/** Maps unique names of external tiles to pseudo-biome IDs. Set on the server,
 * then sent to the clients. <i>Not thread-safe!</i>
 * @author Hunternif
 */
public class ExtTileIdMap extends SaveData {
	public static final ExtTileIdMap INSTANCE = new ExtTileIdMap();

	public static final Identifier
	// Village:
	TILE_VILLAGE_LIBRARY = id("npc_village_library"),
	TILE_VILLAGE_SMITHY = id("npc_village_smithy"),
	TILE_VILLAGE_L_HOUSE = id("npc_village_l_house"),
	TILE_VILLAGE_FARMLAND_SMALL = id("npc_village_farmland_small"),
	TILE_VILLAGE_FARMLAND_LARGE = id("npc_village_farmland_large"),
	TILE_VILLAGE_WELL = id("npc_village_well"),
	TILE_VILLAGE_TORCH = id("npc_village_torch"),
//	TILE_VILLAGE_PATH_X = id("npc_village_path_x"),
//	TILE_VILLAGE_PATH_Z = id("npc_village_path_z"),
	TILE_VILLAGE_HUT = id("npc_village_hut"),
	TILE_VILLAGE_SMALL_HOUSE = id("npc_village_small_house"),
	TILE_VILLAGE_BUTCHERS_SHOP = id("npc_village_butchers_shop"),
	TILE_VILLAGE_CHURCH = id("npc_village_church"),

	TILE_RAVINE = id("ravine"),

	// Nether & Nether Fortress:
	TILE_LAVA = id("lava"),
	TILE_LAVA_SHORE = id("lava_shore"),
	TILE_NETHER_BRIDGE = id("nether_bridge"),
	TILE_NETHER_BRIDGE_X = id("nether_bridge_x"),
	TILE_NETHER_BRIDGE_Z = id("nether_bridge_z"),
	TILE_NETHER_BRIDGE_END_X = id("nether_bridge_end_x"),
	TILE_NETHER_BRIDGE_END_Z = id("nether_bridge_end_z"),
	TILE_NETHER_BRIDGE_GATE = id("nether_bridge_gate"),
	TILE_NETHER_TOWER = id("nether_tower"),
	TILE_NETHER_WALL = id("nether_wall"),
	TILE_NETHER_HALL = id("nether_hall"),
	TILE_NETHER_FORT_STAIRS = id("nether_fort_stairs"),
	TILE_NETHER_THRONE = id("nether_throne"),
	
	TILE_END_ISLAND = id("end_island"),
	TILE_END_ISLAND_PLANTS = id("end_island_plants"),
	TILE_END_VOID = id("end_void");

	private static final Identifier id(String s) {
		return new Identifier("antiqueatlas", s);
	}

	public static final int NOT_FOUND = -1;
	
	/** Set initially to -1 because that is reserved for when no biome is found
	 * or the chunk is not loaded. New IDs are obtained by decrementing lastID. */
	private int lastID = NOT_FOUND;
	private final BiMap<Identifier, Integer> nameToIdMap = HashBiMap.create();
	
	/** Server should call this method when setting tiles.
	 * Clients should not call this method! */
	public int getOrCreatePseudoBiomeID(Identifier uniqueName) {
		Integer id = nameToIdMap.get(uniqueName);
		if (id == null) {
			id = findNewID();
			nameToIdMap.put(uniqueName, id);
			markDirty();
		}

		return id;
	}
	
	/** If the name is not registered, returns {@link #NOT_FOUND} ({@value #NOT_FOUND}). */
	public int getPseudoBiomeID(Identifier uniqueName) {
		Integer id = nameToIdMap.get(uniqueName);
		return id == null ? NOT_FOUND : id.intValue();
	}
	
	public Identifier getPseudoBiomeName(int id) {
		return nameToIdMap.inverse().get(id);
	}
	
	private int findNewID() {
		while (lastID > Short.MIN_VALUE) {
			if (!nameToIdMap.inverse().containsKey(--lastID)) break;
		}

		return lastID;
	}
	
	/** This method must only be called when reading from the config file or
	 *  when executing {@link TileNameS2CPacket}.
	 *  IDs set via this method should not be saved, or client config may become
	 *  inconsistent! */
	public void setPseudoBiomeID(Identifier uniqueName, int id) {
		nameToIdMap.forcePut(uniqueName, id);
	}
	
	/** Map of tile names to biome ID, used for saving config file. */
	Map<Identifier, Integer> getMap() {
		return nameToIdMap;
	}
	
	/** Send all name-biomeID pairs to the player. */
	public void syncOnPlayer(PlayerEntity player) {
		new TileNameS2CPacket(nameToIdMap).send((ServerPlayerEntity) player);
	}
}
