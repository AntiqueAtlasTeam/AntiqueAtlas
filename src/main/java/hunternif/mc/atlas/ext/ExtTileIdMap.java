package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import hunternif.mc.atlas.util.SaveData;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


/** Maps unique names of external tiles to pseudo-biome IDs. Set on the server,
 * then sent to the clients. <i>Not thread-safe!</i>
 * @author Hunternif
 */
public class ExtTileIdMap extends SaveData {
	private static final ExtTileIdMap INSTANCE = new ExtTileIdMap();
	public static ExtTileIdMap instance() {
		return INSTANCE;
	}
	
	public static final String
	// Village:
	TILE_VILLAGE_LIBRARY = "npcVillageLibrary",
	TILE_VILLAGE_SMITHY = "npcVillageSmithy",
	TILE_VILLAGE_L_HOUSE = "npcVillageLHouse",
	TILE_VILLAGE_FARMLAND_SMALL = "npcVillageFarmlandSmall",
	TILE_VILLAGE_FARMLAND_LARGE = "npcVillageFarmlandLarge",
	TILE_VILLAGE_WELL = "npcVillageWell",
	TILE_VILLAGE_TORCH = "npcVillageTorch",
//	TILE_VILLAGE_PATH_X = "npcVillagePathX",
//	TILE_VILLAGE_PATH_Z = "npcVillagePathZ",
	TILE_VILLAGE_HUT = "npcVillageHut",
	TILE_VILLAGE_SMALL_HOUSE = "npcVillageSmallHouse",
	TILE_VILLAGE_BUTCHERS_SHOP = "npcVillageButchersShop",
	TILE_VILLAGE_CHURCH = "npcVillageChurch",
	
	// Nether & Nether Fortress:
	TILE_LAVA = "lava",
	TILE_LAVA_SHORE = "lavaShore",
	TILE_NETHER_BRIDGE = "netherBridge",
	TILE_NETHER_BRIDGE_X = "netherBridgeX",
	TILE_NETHER_BRIDGE_Z = "netherBridgeZ",
	TILE_NETHER_BRIDGE_END_X = "netherBridgeEndX",
	TILE_NETHER_BRIDGE_END_Z = "netherBridgeEndZ",
	TILE_NETHER_BRIDGE_GATE = "netherBridgeGate",
	TILE_NETHER_TOWER = "netherTower",
	TILE_NETHER_WALL = "netherWall",
	TILE_NETHER_HALL = "netherHall",
	TILE_NETHER_FORT_STAIRS = "netherFortStairs",
	TILE_NETHER_THRONE = "netherThrone",
	
	TILE_END_ISLAND = "endIsland",
	TILE_END_ISLAND_PLANTS = "endIslandPlants",
	TILE_END_VOID = "endVoid";
	
	public static final int NOT_FOUND = -1;
	
	/** Set initially to -1 because that is reserved for when no biome is found
	 * or the chunk is not loaded. New IDs are obtained by decrementing lastID. */
	private int lastID = NOT_FOUND;
	private final BiMap<String, Integer> nameToIdMap = HashBiMap.create();
	
	/** Server should call this method when setting tiles.
	 * Clients should not call this method! */
	public int getOrCreatePseudoBiomeID(String uniqueName) {
		Integer id = nameToIdMap.get(uniqueName);
		if (id == null) {
			id = Integer.valueOf(findNewID());
			nameToIdMap.put(uniqueName, id);
			markDirty();
		}
		return id.intValue();
	}
	
	/** If the name is not registered, returns {@link #NOT_FOUND} ({@value #NOT_FOUND}). */
	public int getPseudoBiomeID(String uniqueName) {
		Integer id = nameToIdMap.get(uniqueName);
		return id == null ? NOT_FOUND : id.intValue();
	}
	
	public String getPseudoBiomeName(int id) {
		return nameToIdMap.inverse().get(id);
	}
	
	private int findNewID() {
		while (lastID > Short.MIN_VALUE) {
			if (!nameToIdMap.inverse().containsKey(Integer.valueOf(--lastID))) break;
		}
		return lastID;
	}
	
	/** This method must only be called when reading from the config file or
	 *  when executing {@link TileNameIDPacket}.
	 *  IDs set via this method should not be saved, or client config may become
	 *  inconsistent! */
	public void setPseudoBiomeID(String uniqueName, int id) {
		nameToIdMap.forcePut(uniqueName, Integer.valueOf(id));
	}
	
	/** Map of tile names to biome ID, used for saving config file. */
	Map<String, Integer> getMap() {
		return nameToIdMap;
	}
	
	/** Send all name-biomeID pairs to the player. */
	public void syncOnPlayer(EntityPlayer player) {
		PacketDispatcher.sendTo(new TileNameIDPacket(nameToIdMap), (EntityPlayerMP) player);
	}
}
