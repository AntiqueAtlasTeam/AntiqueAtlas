package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StructureWatcher {
	private final String datFileName;
	private MarkerType marker;
	private MarkerType tileMarker;
	private String markerLabel;
	private String tileMarkerLabel;
	private final int dim;

	public StructureWatcher(String datFileName, int dim, MarkerType marker, String label) {
		this.marker = marker;
		markerLabel = label;
		this.dim = dim;
		this.datFileName = datFileName;
	}
	
	public StructureWatcher setTileMarker(MarkerType type, String label) {
		tileMarker = type;
		tileMarkerLabel = label;
		return this;
	}
	
	/** Set of tag names for every structure, in the format "[x, y]" */
	//TODO: list of visited structures(?) must be reset when changing worlds!
	private final Set<String> visited = new HashSet<>();
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == dim) {
			visitAllUnvisitedStructures(event.getWorld());
		}
	}
	
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == dim) {
			visitAllUnvisitedStructures(event.getWorld());
		}
	}
	
	private void visitAllUnvisitedStructures(World world) {
		MapGenStructureData data = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, datFileName);
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
		Log.info("	Visiting " + datFileName + " in dimension #%d \"%s\" at chunk (%d, %d) ~ blocks (%d, %d)",
				world.provider.getDimension(), world.provider.getDimensionType().getName(),
				chunkX, chunkZ, chunkX << 4, chunkZ << 4);
		boolean foundMarker = false;
		boolean foundTileMarker = false;
		
    	List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
				.getMarkersAtChunk(world.provider.getDimension(), chunkX / MarkersData.CHUNK_STEP, chunkZ / MarkersData.CHUNK_STEP);
		if (markers != null) {
			for (Marker marker : markers) {
				if (!foundMarker && marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ &&
				    marker.getType().equals(this.marker)) {
					foundMarker = true;
				}
				if (!foundTileMarker && tileMarker != null && marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ &&
				    marker.getType().equals(tileMarker)) {
					foundTileMarker = true;
				}
			}
		}
		
		if (SettingsConfig.gameplay.autoVillageMarkers) {
			if(!foundMarker)
				AtlasAPI.markers.putGlobalMarker(world, false, marker.getRegistryName().toString(), markerLabel, (chunkX << 4) + 8, (chunkZ << 4) + 8);
			if(tileMarker != null && !foundTileMarker)
				AtlasAPI.markers.putGlobalMarker(world, false, tileMarker.getRegistryName().toString(), tileMarkerLabel, (chunkX << 4) + 8, (chunkZ << 4) + 8);
		}
	}
	
	private void clearAllMarkers(World world) {
		Collection<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData().getMarkersInDimension(world.provider.getDimension());
		
		for (Marker marker : markers) {
			if(marker.getType().equals(this.marker)) {
				AtlasAPI.markers.deleteGlobalMarker(world, marker.getId());
			}
			if(tileMarker != null && marker.getType().equals(tileMarker)) {
				AtlasAPI.markers.deleteGlobalMarker(world, marker.getId());
			}
		}
	}
}
