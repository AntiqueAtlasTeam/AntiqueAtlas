package hunternif.mc.atlas.ext.watcher.impl;

import com.google.common.collect.Sets;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.ext.watcher.IStructureWatcher;
import hunternif.mc.atlas.ext.watcher.StructureWatcher;
import hunternif.mc.atlas.ext.watcher.WatcherPos;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import none.XX_1_12_2_none_bbw_XX;
import none.XX_1_13_none_bnu_XX;
import org.apache.commons.lang3.tuple.Pair;

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

    @Nonnull
    @Override
    public Set<WatcherPos> getVisited() {
        return visited;
    }

    @Override
    public boolean isDimensionValid(DimensionType type) {
    	return this.dimension == type;
    }

    @Nullable
    @Override
    public CompoundTag getStructureData(@Nonnull World world) {
        XX_1_12_2_none_bbw_XX data = ((ServerWorld) world).getPersistentStateManager().a(XX_1_12_2_none_bbw_XX.class, datFileName);
        if (data == null)
            return null;

        return data.a();
    }

    @Nonnull
    @Override
    public Set<Pair<WatcherPos, String>> visitStructure(@Nonnull World world, @Nonnull CompoundTag structureTag) {
        Set<Pair<WatcherPos, String>> visits = Sets.newHashSet();
        Set<String> tagSet = structureTag.getKeys();
        for (String coords : tagSet) {
            if (!WatcherPos.POS_PATTERN.matcher(coords).matches())
                continue; // Some other kind of data got stuffed in here. It's irrelevant to us

            WatcherPos pos = new WatcherPos(coords);
            if (!visited.contains(pos)) {
                CompoundTag tag = structureTag.getCompound(coords);
                visit(world, tag);
                visited.add(pos);
                visits.add(Pair.of(pos, datFileName));
            }
        }
        return visits;
    }

    public StructureWatcherGeneric setTileMarker(MarkerType type, String label) {
        tileMarker = type;
        tileMarkerLabel = label;
        return this;
    }
	
	private void visit(World world, CompoundTag tag) {
		int chunkX = tag.getInt("ChunkX");
		int chunkZ = tag.getInt("ChunkZ");
		boolean foundMarker = false;
		boolean foundTileMarker = false;
		
    	List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
				.getMarkersAtChunk(world.dimension.getType(), chunkX / MarkersData.CHUNK_STEP, chunkZ / MarkersData.CHUNK_STEP);
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
				AtlasAPI.markers.putGlobalMarker(world, false, MarkerRegistry.getId(marker).toString(), markerLabel, (chunkX << 4) + 8, (chunkZ << 4) + 8);
			if(tileMarker != null && !foundTileMarker)
				AtlasAPI.markers.putGlobalMarker(world, false, MarkerRegistry.getId(tileMarker).toString(), tileMarkerLabel, (chunkX << 4) + 8, (chunkZ << 4) + 8);
		}
	}
}
