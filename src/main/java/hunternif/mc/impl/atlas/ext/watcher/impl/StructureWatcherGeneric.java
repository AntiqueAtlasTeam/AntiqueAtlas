package hunternif.mc.impl.atlas.ext.watcher.impl;

import com.google.common.collect.Sets;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.ext.watcher.IStructureWatcher;
import hunternif.mc.impl.atlas.ext.watcher.StructureWatcher;
import hunternif.mc.impl.atlas.ext.watcher.WatcherPos;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StructureWatcherGeneric implements IStructureWatcher {

    private final Set<WatcherPos> visited = new HashSet<>();
	private final String datFileName;
	private MarkerType markerType;
	private MarkerType tileMarker;
	private ITextComponent markerLabel;
	private ITextComponent tileMarkerLabel;
	private final RegistryKey<World> dimension;

    public StructureWatcherGeneric(String datFileName, RegistryKey<World> dimType, MarkerType markerType, ITextComponent label) {
        this.markerType = markerType;
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
    public boolean isDimensionValid(ServerWorld world) {
       	return this.dimension == world.getDimensionKey();
    }

    @Nullable
    @Override
    public CompoundNBT getStructureData(@Nonnull ServerWorld world) {
        /* XX_1_12_2_none_bbw_XX data = ((ServerWorld) world).getPersistentStateManager().a(XX_1_12_2_none_bbw_XX.class, datFileName);
        if (data == null)
            return null;

        return data.a(); */

        // TODO FABRIC
        return null;
    }

    @Nonnull
    @Override
    public Set<Pair<WatcherPos, String>> visitStructure(@Nonnull World world, @Nonnull CompoundNBT structureTag) {
        Set<Pair<WatcherPos, String>> visits = Sets.newHashSet();
        Set<String> tagSet = structureTag.keySet();
        for (String coords : tagSet) {
            if (!WatcherPos.POS_PATTERN.matcher(coords).matches())
                continue; // Some other kind of data got stuffed in here. It's irrelevant to us

            WatcherPos pos = new WatcherPos(coords);
            if (!visited.contains(pos)) {
                CompoundNBT tag = structureTag.getCompound(coords);
                visit(world, tag);
                visited.add(pos);
                visits.add(Pair.of(pos, datFileName));
            }
        }
        return visits;
    }

    public StructureWatcherGeneric setTileMarker(MarkerType type, ITextComponent label) {
        tileMarker = type;
        tileMarkerLabel = label;
        return this;
    }
	
	private void visit(World world, CompoundNBT tag) {
		int chunkX = tag.getInt("ChunkX");
		int chunkZ = tag.getInt("ChunkZ");
		boolean foundMarker = false;
		boolean foundTileMarker = false;
		
    	List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
				.getMarkersAtChunk(world.getDimensionKey(), chunkX / MarkersData.CHUNK_STEP, chunkZ / MarkersData.CHUNK_STEP);
		if (markers != null) {
			for (Marker marker : markers) {
				if (!foundMarker && marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ &&
				    marker.getType().equals(this.markerType)) {
					foundMarker = true;
				}
				if (!foundTileMarker && tileMarker != null && marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ &&
				    marker.getType().equals(tileMarker)) {
					foundTileMarker = true;
				}
			}
		}
		
		if (AntiqueAtlasConfig.autoVillageMarkers.get()) {
			if(!foundMarker)
				AtlasAPI.markers.putGlobalMarker(world, false, markerType, markerLabel, (chunkX << 4) + 8, (chunkZ << 4) + 8);
			if(tileMarker != null && !foundTileMarker)
				AtlasAPI.markers.putGlobalMarker(world, false, markerType, tileMarkerLabel, (chunkX << 4) + 8, (chunkZ << 4) + 8);
		}
	}
}
