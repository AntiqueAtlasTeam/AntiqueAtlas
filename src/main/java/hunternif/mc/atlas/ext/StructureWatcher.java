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
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;

public class StructureWatcher {
	public final String DAT;
	public MarkerType MARKER, TILE_MARKER;
	public String MARKER_NAME, TILE_MARKER_NAME;
	public final int DIM;
	
	public StructureWatcher(String dat, int dim, MarkerType marker, String name) {
		MARKER = marker;
		MARKER_NAME = name;
		DIM = dim;
		DAT = dat;
	}
	
	public StructureWatcher setTileMarker(MarkerType type, String name) {
		TILE_MARKER = type;
		TILE_MARKER_NAME = name;
		return this;
	}
	
	/** Set of tag names for every structure, in the format "[x, y]" */
	//TODO: list of visited villages must be reset when changing worlds!
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
		Log.info("	Visiting " + DAT + " in dimension #%d \"%s\" at chunk (%d, %d) ~ blocks (%d, %d)",
				world.provider.getDimension(), world.provider.getDimensionType().getName(),
				chunkX, chunkZ, chunkX << 4, chunkZ << 4);
		boolean foundMarker = false;
		boolean foundTileMarker = false;
		
    	List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
				.getMarkersAtChunk(world.provider.getDimension(), chunkX / MarkersData.CHUNK_STEP, chunkZ / MarkersData.CHUNK_STEP);
		if (markers != null) {
			for (Marker marker : markers) {
				if (!foundMarker && marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ &&
				    marker.getType().equals(MARKER)) {
					foundMarker = true;
				}
				if (!foundTileMarker && TILE_MARKER != null && marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ &&
				    marker.getType().equals(TILE_MARKER)) {
					foundTileMarker = true;
				}
			}
		}
		
		if (AntiqueAtlasMod.settings.autoVillageMarkers) {
			if(!foundMarker)
				AtlasAPI.markers.putGlobalMarker(world, false, MARKER, MARKER_NAME, (chunkX << 4) + 8, (chunkZ << 4) + 8);
			if(TILE_MARKER != null && !foundTileMarker)
				AtlasAPI.markers.putGlobalMarker(world, false, TILE_MARKER, TILE_MARKER_NAME, (chunkX << 4) + 8, (chunkZ << 4) + 8);
		}
	}
	
	private void clearAllMarkers(World world) {
		Collection<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData().getMarkersInDimension(world.provider.getDimension());
		
		for (Marker marker : markers) {
			if(marker.getType().equals(MARKER)) {
				AtlasAPI.markers.deleteGlobalMarker(world, marker.getId());
			}
			if(TILE_MARKER != null && marker.getType().equals(TILE_MARKER)) {
				AtlasAPI.markers.deleteGlobalMarker(world, marker.getId());
			}
		}
	}
}
