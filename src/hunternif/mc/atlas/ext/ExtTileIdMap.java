package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


/** Maps unique names of external tiles to pseudo-biome IDs. Set on the server,
 * then sent to the clients. <i>Not thread-safe!</i>
 * @author Hunternif
 */
public enum ExtTileIdMap {
	INSTANCE;
	public static ExtTileIdMap instance() {
		return INSTANCE;
	}
	
	public static final String TILE_VILLAGE_HOUSE = "npcVillageDoor";
	public static final String TILE_VILLAGE_TERRITORY = "npcVillageTerritory";
	
	/** Set initially to -1 because that is reserved for when no biome is found
	 * or the chunk is not loaded. New IDs are obtained by decrementing lastID. */
	private int lastID = ChunkBiomeAnalyzer.NOT_FOUND;
	private final BiMap<String, Integer> nameToIdMap = HashBiMap.create();
	
	/** Server should call this method when setting tiles.
	 * Clients should not call this method! */
	public int getOrCreatePseudoBiomeID(String uniqueName) {
		Integer id = nameToIdMap.get(uniqueName);
		if (id == null) {
			id = Integer.valueOf(findNewID());
			nameToIdMap.put(uniqueName, id);
			AntiqueAtlasMod.proxy.updateExtTileConfig();
			//TODO send packet to clients
		}
		return id.intValue();
	}
	
	/** If the name is not registered, returns -1. */
	public int getPseudoBiomeID(String uniqueName) {
		Integer id = nameToIdMap.get(uniqueName);
		return id == null ? ChunkBiomeAnalyzer.NOT_FOUND : id.intValue();
	}
	
	private int findNewID() {
		while (lastID > Short.MIN_VALUE) {
			if (!nameToIdMap.inverse().containsKey(Integer.valueOf(--lastID))) break;
		}
		return lastID;
	}
	
	/** Map of unique tile name to pseudo biome ID. */
	BiMap<String, Integer> getMap() {
		return nameToIdMap;
	}
	void setPseudoBiomeID(String uniqueName, int id) {
		nameToIdMap.put(uniqueName, Integer.valueOf(id));
	}
}
