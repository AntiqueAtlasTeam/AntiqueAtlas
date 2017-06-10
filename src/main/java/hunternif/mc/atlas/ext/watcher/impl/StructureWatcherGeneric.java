package hunternif.mc.atlas.ext.watcher.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.ext.watcher.IStructureWatcher;
import hunternif.mc.atlas.ext.watcher.StructureWatcher;
import hunternif.mc.atlas.ext.watcher.WatcherPos;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StructureWatcherGeneric implements IStructureWatcher {

    private final Set<WatcherPos> visited = new HashSet<>();
	private final String datFileName;
	private MarkerType marker;
	private MarkerType tileMarker;
	private String markerLabel;
	private String tileMarkerLabel;
	private final DimensionType dimension;

    public StructureWatcherGeneric(String datFileName, DimensionType dimType, MarkerType marker, String label) {
        this.marker = marker;
        this.markerLabel = label;
        this.dimension = dimType;
        this.datFileName = datFileName;

        StructureWatcher.INSTANCE.addWatcher(this);
    }

    public StructureWatcherGeneric(String datFileName, int dimension, MarkerType marker, String label) {
        this(datFileName, DimensionManager.getProviderType(dimension), marker, label);
    }

    @Nonnull
    @Override
    public Set<WatcherPos> getVisited() {
        return visited;
    }

    @Override
    public boolean isDimensionValid(DimensionType type) {
        return type.getId() == dimension.getId(); // Only in provided dimension
    }

    @Nullable
    @Override
    public NBTTagCompound getStructureData(@Nonnull World world) {
        MapGenStructureData data = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, datFileName);
        if (data == null)
            return null;

        return data.getTagCompound();
    }

    @Override
    public void visitStructure(@Nonnull World world, @Nonnull NBTTagCompound structureTag) {
        Set<String> tagSet = structureTag.getKeySet();
        for (String coords : tagSet) {
            WatcherPos pos = new WatcherPos(coords);
            if (!visited.contains(pos)) {
                NBTTagCompound tag = structureTag.getCompoundTag(coords);
                visit(world, tag);
                visited.add(pos);
            }
        }
    }

    public StructureWatcherGeneric setTileMarker(MarkerType type, String label) {
        tileMarker = type;
        tileMarkerLabel = label;
        return this;
    }
	
	private void visit(World world, NBTTagCompound tag) {
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
}
