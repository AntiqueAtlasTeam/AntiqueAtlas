package hunternif.mc.atlas.ext;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hunternif.mc.atlas.util.MathUtil;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerTypes;
import hunternif.mc.atlas.util.Log;

public class VillageWatcher {
	/** Set of tag names for every village, in the format "[x, y]" */
	//TODO: list of visited villages must be reset when changing worlds!
	// And the same goes for Nether Fortress Watcher
	private final Set<String> visited = new HashSet<>();
	
	private static final String LIBRARY= "ViBH"; // Big-ish house with a high slanted roof, large windows; books and couches inside.
	private static final String SMITHY = "ViS"; // The smithy.
	private static final String L_HOUSE = "ViTRH"; // Medium-sized, L-shaped houses with slanted roof.
	
	private static final String FARMLAND_LARGE = "ViDF"; // Large field, basically 2 times larger than FIELD2
	private static final String FARMLAND_SMALL = "ViF"; // Smaller field.
	
	@SuppressWarnings("unused")
	private static final String PATH = "ViSR"; // Usually too narrow to be seen between the houses...
	private static final String TORCH = "ViL";
	private static final String WELL = "ViW";
	private static final String START = "ViStart"; // Same as WELL
	
	private static final String BUTCHERS = "ViPH"; // Medium house with a low slanted roof and a dirt porch with a fence.
	private static final String HUT = "ViSmH"; // Tiniest hut with a flat roof.
	private static final String HOUSE_SMALL = "ViSH"; // Slightly larger than huts, sometimes with a fenced balcony on the roof and a ladder.
	private static final String CHURCH = "ViST"; // The church.
	
	private static final Map<String, String> partToTileMap;
	static {
		ImmutableMap.Builder<String, String> builder = new Builder<>();
		builder.put(LIBRARY, ExtTileIdMap.TILE_VILLAGE_LIBRARY);
		builder.put(SMITHY, ExtTileIdMap.TILE_VILLAGE_SMITHY);
		builder.put(L_HOUSE, ExtTileIdMap.TILE_VILLAGE_L_HOUSE);
		builder.put(FARMLAND_LARGE, ExtTileIdMap.TILE_VILLAGE_FARMLAND_LARGE);
		builder.put(FARMLAND_SMALL, ExtTileIdMap.TILE_VILLAGE_FARMLAND_SMALL);
//		builder.put(PATH, ExtTileIdMap.TILE_VILLAGE_PATH_X); // Handled separately
		builder.put(TORCH, ExtTileIdMap.TILE_VILLAGE_TORCH);
		builder.put(WELL, ExtTileIdMap.TILE_VILLAGE_WELL);
		builder.put(START, ExtTileIdMap.TILE_VILLAGE_WELL);
		builder.put(BUTCHERS, ExtTileIdMap.TILE_VILLAGE_BUTCHERS_SHOP);
		builder.put(HOUSE_SMALL, ExtTileIdMap.TILE_VILLAGE_SMALL_HOUSE);
		builder.put(HUT, ExtTileIdMap.TILE_VILLAGE_HUT);
		builder.put(CHURCH, ExtTileIdMap.TILE_VILLAGE_CHURCH);
		partToTileMap = builder.build();
	}
	/** Tiles with the higher priority override tiles with lower priority at the same chunk. */
	private static final Map<String, Integer> tilePriority;
	static {
		ImmutableMap.Builder<String, Integer> builder = new Builder<>();
		builder.put(ExtTileIdMap.TILE_VILLAGE_LIBRARY, 5);
		builder.put(ExtTileIdMap.TILE_VILLAGE_SMITHY, 6);
		builder.put(ExtTileIdMap.TILE_VILLAGE_L_HOUSE, 5);
		builder.put(ExtTileIdMap.TILE_VILLAGE_FARMLAND_LARGE, 3);
		builder.put(ExtTileIdMap.TILE_VILLAGE_FARMLAND_SMALL, 3);
//		builder.put(ExtTileIdMap.TILE_VILLAGE_PATH_X, 0);
//		builder.put(ExtTileIdMap.TILE_VILLAGE_PATH_Z, 0);
		builder.put(ExtTileIdMap.TILE_VILLAGE_TORCH, 1);
		builder.put(ExtTileIdMap.TILE_VILLAGE_WELL, 7);
		builder.put(ExtTileIdMap.TILE_VILLAGE_BUTCHERS_SHOP, 4);
		builder.put(ExtTileIdMap.TILE_VILLAGE_SMALL_HOUSE, 4);
		builder.put(ExtTileIdMap.TILE_VILLAGE_HUT, 3);
		builder.put(ExtTileIdMap.TILE_VILLAGE_CHURCH, 6);
		tilePriority = builder.build();
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
			visitAllUnvisitedVillages(event.getWorld());
		}
	}
	
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
			visitAllUnvisitedVillages(event.getWorld());
		}
	}
	
	private void visitAllUnvisitedVillages(World world) {
		MapGenStructureData data = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, "Village");
		if (data == null) return;
		NBTTagCompound villageNBTData = data.getTagCompound();
		Set<String> tagSet = villageNBTData.getKeySet();
		for (String coords : tagSet) {
			if (!visited.contains(coords)) {
				NBTBase tag = villageNBTData.getTag(coords);
				if (tag.getId() == 10) { // is NBTTagCompound
					visitVillage(world, (NBTTagCompound) tag);
					visited.add(coords);
				}
			}
		}
	}
	
	/** Put all child parts of the fortress on the map as global custom tiles. */
	private void visitVillage(World world, NBTTagCompound tag) {
		if (!tag.getBoolean("Valid")) {
			// The village was not actually generated and should not be mapped.
			// Remove legacy marker and custom tile:
			removeVillage(world, tag);
			return;
		}
		int startChunkX = tag.getInteger("ChunkX");
		int startChunkZ = tag.getInteger("ChunkZ");
		Log.info("Visiting NPC Village in dimension #%d \"%s\" at chunk (%d, %d) ~ blocks (%d, %d)",
				world.provider.getDimension(), world.provider.getDimensionType().getName(),
				startChunkX, startChunkZ, startChunkX << 4, startChunkZ << 4);
		NBTTagList children = tag.getTagList("Children", 10);
		for (int i = 0; i < children.tagCount(); i++) {
			NBTTagCompound child = children.getCompoundTagAt(i);
			String childID = child.getString("id");
			StructureBoundingBox boundingBox = new StructureBoundingBox(child.getIntArray("BB"));
			int x = MathUtil.getCenter(boundingBox).getX();
			int z = MathUtil.getCenter(boundingBox).getZ();
			int chunkX = x >> 4;
			int chunkZ = z >> 4;
			if (START.equals(childID)) {
				// Put marker at Start.
				// Check if the marker already exists:
				boolean foundMarker = false;
				// Legacy support: don't place new marker if there's already one in a wider area.
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
								.getMarkersAtChunk(world.provider.getDimension(), j + chunkX / MarkersData.CHUNK_STEP, k + chunkZ / MarkersData.CHUNK_STEP);
						if (markers != null) {
							for (Marker marker : markers) {
								if (marker.getType().equals(MarkerTypes.VILLAGE)) {
									foundMarker = true;
									break;
								}
							}
						}
					}
				}
				if (!foundMarker && AntiqueAtlasMod.settings.autoVillageMarkers) {
					AtlasAPI.markers.putGlobalMarker(world, false, MarkerTypes.VILLAGE, "gui.antiqueatlas.marker.village", x, z);
				}
			}
//			String tileName = null;
//			if (PATH.equals(childID)) {
//				int orientation = child.getInteger("O");
//				switch (orientation) {
//				case 0:
//				case 2: tileName = ExtTileIdMap.TILE_VILLAGE_PATH_Z; break;
//				case 1:
//				case 3: tileName = ExtTileIdMap.TILE_VILLAGE_PATH_X; break;
//				}
//			} else {
//			}
			String tileName = partToTileMap.get(childID);
			if (tileName != null) {
				Integer curTilePriority = tilePriority.get(tileName);
				Integer prevTilePriority = tilePriority.get(tileAt(chunkX, chunkZ));
				if (curTilePriority != null && prevTilePriority != null) {
					if (curTilePriority >= prevTilePriority) {
						AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
					}
				} else {
					AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				}
			}
		}
	}
	
	private static String tileAt(int chunkX, int chunkZ) {
		int biomeID = AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(0, chunkX, chunkZ);
		return ExtTileIdMap.instance().getPseudoBiomeName(biomeID);
	}
	
	/** Delete the marker and custom tile data about the village. */
	private static void removeVillage(World world, NBTTagCompound tag) {
		NBTTagList children = tag.getTagList("Children", 10);
		for (int i = 0; i < children.tagCount(); i++) {
			NBTTagCompound child = children.getCompoundTagAt(i);
			String childID = child.getString("id");
			StructureBoundingBox boundingBox = new StructureBoundingBox(child.getIntArray("BB"));
			int x = MathUtil.getCenter(boundingBox).getX();
			int z = MathUtil.getCenter(boundingBox).getZ();
			int chunkX = x >> 4;
			int chunkZ = z >> 4;
			if (START.equals(childID)) {
				List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
						.getMarkersAtChunk(world.provider.getDimension(), chunkX / MarkersData.CHUNK_STEP, chunkZ / MarkersData.CHUNK_STEP);
				if (markers != null) {
					for (Marker marker : markers) {
						if (marker.getType().equals(MarkerTypes.VILLAGE)) {
							AtlasAPI.markers.deleteGlobalMarker(world, marker.getId());
							Log.info("Removed faux village marker");
							break;
						}
					}
				}
			}
			AtlasAPI.tiles.deleteCustomGlobalTile(world, chunkX, chunkZ);
			Log.info("Removed faux village tile");
		}
	}
}
