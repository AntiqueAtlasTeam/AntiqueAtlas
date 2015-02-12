package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class VillageWatcher {
	public static final String MARKER = "village";
	
	/** Set of tag names for every village, in the format "[x, y]" */
	private final Set<String> visited = new HashSet<String>();
	
	private static final String HOUSE1 = "ViBH"; // Big-ish house with a high slanted roof, large windows; books and couches inside.
	private static final String SMITHY = "ViS"; // The smithy.
	private static final String HOUSE3 = "ViTRH"; // Medium-sized, L-shaped houses with slanted roof.
	
	private static final String FIELD1 = "ViDF"; // Large field, basically 2 times larger than FIELD2
	private static final String FIELD2 = "ViF"; // Smaller field.
	
	private static final String PATH = "ViSR"; // Usually too narrow to be seen between the houses...
	private static final String TORCH = "ViL";
	private static final String WELL = "ViW";
	private static final String START = "ViStart"; // Same as WELL
	
	private static final String HALL = "ViPH"; // Medium house with a low slanted roof and a dirt porch with a fence.
	private static final String GARDEN = "ViSH"; // Slightly larger huts with fence railings on the roof and a ladder leading to it.
	private static final String HUT = "ViSmH"; // Tiniest hut with a flat roof.
	private static final String CHURCH = "ViST"; // The church.
	
	
	private static final Map<String, String> partToTileMap;
	static {
		ImmutableMap.Builder<String, String> builder = new Builder<String, String>();
		builder.put(HOUSE1, ExtTileIdMap.TILE_VILLAGE_HOUSE1);
		builder.put(SMITHY, ExtTileIdMap.TILE_NETHER_HALL);
		builder.put(HOUSE3, ExtTileIdMap.TILE_VILLAGE_HOUSE2);
		builder.put(FIELD1, ExtTileIdMap.TILE_VILLAGE_TERRITORY);
		builder.put(FIELD2, ExtTileIdMap.TILE_VILLAGE_TERRITORY);
		builder.put(PATH, ExtTileIdMap.TILE_VILLAGE_TERRITORY);
		builder.put(TORCH, ExtTileIdMap.TILE_VILLAGE_TERRITORY);
		builder.put(WELL, ExtTileIdMap.TILE_VILLAGE_TERRITORY);
		builder.put(START, ExtTileIdMap.TILE_VILLAGE_TERRITORY);
		builder.put(HALL, ExtTileIdMap.TILE_VILLAGE_HOUSE1);
		builder.put(GARDEN, ExtTileIdMap.TILE_VILLAGE_HOUSE2);
		builder.put(HUT, ExtTileIdMap.TILE_VILLAGE_HOUSE1);
		builder.put(CHURCH, ExtTileIdMap.TILE_NETHER_TOWER);
		partToTileMap = builder.build();
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
			visitAllUnvisitedVillages(event.world);
		}
	}
	
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
			visitAllUnvisitedVillages(event.world);
		}
	}
	
	public void visitAllUnvisitedVillages(World world) {
		MapGenStructureData data = (MapGenStructureData)world.perWorldStorage.loadData(MapGenStructureData.class, "Village");
		if (data == null) return;
		NBTTagCompound fortressNBTData = data.func_143041_a();
		@SuppressWarnings("unchecked")
		Set<String> tagSet = fortressNBTData.func_150296_c();
		for (String coords : tagSet) {
			if (!visited.contains(coords)) {
				NBTBase tag = fortressNBTData.getTag(coords);
				if (tag.getId() == 10) { // is NBTTagCompound
					visitVillage(world, (NBTTagCompound) tag);
					visited.add(coords);
				}
			}
		}
	}
	
	/** Put all child parts of the fortress on the map as global custom tiles. */
	private void visitVillage(World world, NBTTagCompound tag) {
		NBTTagList children = tag.getTagList("Children", 10);
		for (int i = 0; i < children.tagCount(); i++) {
			NBTTagCompound child = children.getCompoundTagAt(i);
			String childID = child.getString("id");
			StructureBoundingBox boundingBox = new StructureBoundingBox(child.getIntArray("BB"));
			int x = boundingBox.getCenterX();
			int z = boundingBox.getCenterZ();
			int chunkX = x >> 4;
			int chunkZ = z >> 4;
			if (START.equals(childID)) {
				// Put marker at Start.
				// Check if the marker already exists:
				boolean foundMarker = false;
				List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
						.getMarkersAtChunk(0, chunkX / MarkersData.CHUNK_STEP, chunkZ / MarkersData.CHUNK_STEP);
				if (markers != null) {
					for (Marker marker : markers) {
						if (marker.getType().equals(MARKER)) {
							foundMarker = true;
							break;
						}
					}
				}
				if (!foundMarker) {
					AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, MARKER, "gui.antiqueatlas.marker.village", x, z);
				}
			}
			String tileName = partToTileMap.get(childID);
			AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
		}
	}
}
