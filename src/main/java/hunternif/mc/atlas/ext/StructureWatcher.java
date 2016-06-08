package hunternif.mc.atlas.ext;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.util.Log;

public class StructureWatcher {
	public final String MARKER, DAT, MARKER_NAME;
	public final int DIM;
	
	public StructureWatcher(String marker, String markerName, int dim, String dat) {
		MARKER = marker;
		MARKER_NAME = markerName;
		DIM = dim;
		DAT = dat;
	}
	
	/** Set of tag names for every structure, in the format "[x, y]" */
	//TODO: list of visited villages must be reset when changing worlds!
	// And the same goes for Nether Fortress Watcher
	private final Set<String> visited = new HashSet<String>();
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == DIM) {
			visitAllUnvisitedStructures(event.getWorld());
		}
	}
	
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == DIM) {
			visitAllUnvisitedStructures(event.getWorld());
		}
	}
	
	public void visitAllUnvisitedStructures(World world) {
		MapGenStructureData data = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, DAT);
		if (data == null) return;
		NBTTagCompound nbtData = data.getTagCompound();
		Set<String> tagSet = nbtData.getKeySet();
		for (String coords : tagSet) {
			if (!visited.contains(coords)) {
				NBTBase tag = nbtData.getTag(coords);
				if (tag.getId() == 10) { // is NBTTagCompound
					visitStructure(world, (NBTTagCompound) tag);
					visited.add(coords);
				}
			}
		}
	}
	
	private void visitStructure(World world, NBTTagCompound tag) {
		int chunkX = tag.getInteger("ChunkX");
		int chunkZ = tag.getInteger("ChunkZ");
		Log.info("	Visiting " + MARKER + " in dimension #%d \"%s\" at chunk (%d, %d) ~ blocks (%d, %d)",
				world.provider.getDimension(), world.provider.getDimensionType().getName(),
				chunkX, chunkZ, chunkX << 4, chunkZ << 4);
		boolean foundMarker = false;
		
    	List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
				.getMarkersAtChunk(world.provider.getDimension(), chunkX / MarkersData.CHUNK_STEP, chunkZ / MarkersData.CHUNK_STEP);
		if (markers != null) {
			for (Marker marker : markers) {
				if (marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ &&
				    marker.getType().equals(MARKER)) {
					foundMarker = true;
					break;
				}
			}
		}
    	
		if(foundMarker)
			return;
		
		if (AntiqueAtlasMod.settings.autoVillageMarkers) {
			AtlasAPI.markers.putGlobalMarker(world, false, MARKER, MARKER_NAME + ".markerthing", (chunkX << 4) + 8, (chunkZ << 4) + 8);
		}
	}
	
	private void clearAllMarkers(World world) {
		Collection<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData().getMarkersInDimension(world.provider.getDimension());
		
		for (Marker marker : markers) {
			if(marker.getType().equals(MARKER)) {
				AtlasAPI.markers.deleteGlobalMarker(world, marker.getId());
			}
		}
	}
}
