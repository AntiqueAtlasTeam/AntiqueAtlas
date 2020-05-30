package hunternif.mc.atlas.ext.watcher.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.watcher.IStructureWatcher;
import hunternif.mc.atlas.ext.watcher.StructureWatcher;
import hunternif.mc.atlas.ext.watcher.WatcherPos;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.MathUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StructureWatcherVillage implements IStructureWatcher {
	public static final String MARKER = "village";

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

	private static final Map<String, ResourceLocation> partToTileMap;
	static {
		ImmutableMap.Builder<String, ResourceLocation> builder = new Builder<>();
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
	private static final Map<ResourceLocation, Integer> tilePriority;
	static {
		ImmutableMap.Builder<ResourceLocation, Integer> builder = new Builder<>();
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

    private final Set<WatcherPos> visited = new HashSet<>();

    public StructureWatcherVillage() {
	    StructureWatcher.INSTANCE.addWatcher(this);
    }

    @Nonnull
    @Override
    public Set<WatcherPos> getVisited() {
        return visited;
    }

    @Override
    public boolean isDimensionValid(DimensionType type) {
    	return type == DimensionType.OVERWORLD;
    }

    @Nullable
    @Override
    public CompoundNBT getStructureData(@Nonnull World world) {
	    /* XX_1_12_2_none_bbw_XX data = ((ServerWorld) world).getPersistentStateManager().a(XX_1_12_2_none_bbw_XX.class, "Village");
        if (data == null)
            return null;

        return data.a(); */
		// TODO FABRIC
		return null;
    }

    @Nonnull
    @Override
    public Set<Pair<WatcherPos, String>> visitStructure(@Nonnull World world, @Nonnull CompoundNBT structureTag) {
        Set<String> tagSet = structureTag.keySet();
        Set<Pair<WatcherPos, String>> visits = Sets.newHashSet();
        for (String coords : tagSet) {
            if (!WatcherPos.POS_PATTERN.matcher(coords).matches())
                continue; // Some other kind of data got stuffed in here. It's irrelevant to us

            WatcherPos pos = new WatcherPos(coords);
            if (!visited.contains(pos)) {
                CompoundNBT tag = structureTag.getCompound(coords);
                visitVillage(world, tag);
                visited.add(pos);
                visits.add(Pair.of(pos, "Village"));
            }
        }
        return visits;
    }

	/** Put all child parts of the fortress on the map as global custom tiles. */
	private void visitVillage(World world, CompoundNBT tag) {
		if (!tag.getBoolean("Valid")) {
			// The village was not actually generated and should not be mapped.
			// Remove legacy marker and custom tile:
			removeVillage(world, tag);
			return;
		}

		MarkerType villageType = MarkerRegistry.find("antiqueatlas:village");
		if (villageType == null) {
			return;
		}

		ListNBT children = tag.getList("Children", 10);
		for (int i = 0; i < children.size(); i++) {
			CompoundNBT child = children.getCompound(i);
			String childID = child.getString("id");
			MutableBoundingBox boundingBox = new MutableBoundingBox(child.getIntArray("BB"));
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
								.getMarkersAtChunk(world.dimension.getType(), j + chunkX / MarkersData.CHUNK_STEP, k + chunkZ / MarkersData.CHUNK_STEP);
						if (markers != null) {
							for (Marker marker : markers) {
								if (marker.getType().equals(villageType)) {
									foundMarker = true;
									break;
								}
							}
						}
					}
				}
				if (!foundMarker && SettingsConfig.autoVillageMarkers) {
					AtlasAPI.markers.putGlobalMarker(world, false, MarkerRegistry.getId(villageType).toString(), "gui.antiqueatlas.marker.village", x, z);
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
			ResourceLocation tileName = partToTileMap.get(childID);
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

	private static ResourceLocation tileAt(int chunkX, int chunkZ) {
		int biomeID = AntiqueAtlasMod.extBiomeData.getData().getBiomeAt(DimensionType.OVERWORLD, chunkX, chunkZ);
		return ExtTileIdMap.instance().getPseudoBiomeName(biomeID);
	}

	/** Delete the marker and custom tile data about the village. */
	private static void removeVillage(World world, CompoundNBT tag) {
		ListNBT children = tag.getList("Children", 10);
		for (int i = 0; i < children.size(); i++) {
			CompoundNBT child = children.getCompound(i);
			String childID = child.getString("id");
			MutableBoundingBox boundingBox = new MutableBoundingBox(child.getIntArray("BB"));
			int x = MathUtil.getCenter(boundingBox).getX();
			int z = MathUtil.getCenter(boundingBox).getZ();
			int chunkX = x >> 4;
			int chunkZ = z >> 4;
			if (START.equals(childID)) {
				List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData()
						.getMarkersAtChunk(world.dimension.getType(), chunkX / MarkersData.CHUNK_STEP, chunkZ / MarkersData.CHUNK_STEP);
				if (markers != null) {
					for (Marker marker : markers) {
						if (marker.getType().equals("antiqueatlas:village")) {
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
