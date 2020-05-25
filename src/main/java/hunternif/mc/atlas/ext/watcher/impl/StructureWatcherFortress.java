package hunternif.mc.atlas.ext.watcher.impl;

import com.google.common.collect.Sets;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.watcher.IStructureWatcher;
import hunternif.mc.atlas.ext.watcher.StructureWatcher;
import hunternif.mc.atlas.ext.watcher.WatcherPos;
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
import java.util.Set;

@SuppressWarnings("unused")
public class StructureWatcherFortress implements IStructureWatcher {
	// Corridors:
	private static final String ROOFED = "NeSCLT"; // Roofed corridor, solid wall down to the ground
	private static final String ROOFED2 = "NeSCR"; // Another roofed corridor? i guess
	private static final String ROOFED_STAIRS = "NeCCS"; // Roofed stairs, solid wall down to the ground
	private static final String ROOFED3 = "NeCTB"; // Really small roofed corridor
	private static final String ROOFED4 = "NeSC"; // ? Roofed? Covers most of the area of the Fortress. Solid wall down to the ground?

	// Crossings:
	private static final String BRIDGE_GATE = "NeRC"; // That room with no roof with gates facing each direction. One thick solid column going down to the ground. -done!
	private static final String ROOFED_CROSS = "NeSCSC"; // Roofed corridor?
	private static final String BRIDGE_CROSS = "NeBCr"; // A crossing of open bridges. No roof, no column. Takes up 19x19 area because of the beginnings of bridges starting off in different directions. - done!
	private static final String START = "NeStart"; // The same as "NeBCr" - done!

	// Bridges:
	private static final String BRIDGE = "NeBS"; // "19-block-long section of the bridge with columns, no roof. -done!
	private static final String BRIDGE_END = "NeBEF"; // The ruined end of a bridge - done!

	private static final String ENTRANCE = "NeCE"; // "Entrance", a very large room with an iron-barred gate. Contains a well of lava in the center.
	private static final String WART_STAIRS = "NeCSR"; // Room with the Nether Wart and a wide staircase leading to an open roof with a fence railing.
	private static final String THRONE = "NeMT"; // Blaze spawner. No roof. A decorative wall of fence ("the throne"?)
	private static final String TOWER = "NeSR"; // That room with tiny stairs going up to the roof along the wall -done!

    private final Set<WatcherPos> visited = new HashSet<>();

    public StructureWatcherFortress() {
        StructureWatcher.INSTANCE.addWatcher(this);
    }

    @Nonnull
    @Override
    public Set<WatcherPos> getVisited() {
        return visited;
    }

    @Override
    public boolean isDimensionValid(DimensionType type) {
        return type == DimensionType.THE_NETHER;
    }

    @Nullable
    @Override
    public CompoundNBT getStructureData(@Nonnull World world) {
        /* XX_1_12_2_none_bbw_XX data = ((ServerWorld) world).getPersistentStateManager().a(XX_1_12_2_none_bbw_XX.class, "Fortress");
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
                visitFortress(world, tag);
                visited.add(pos);
                visits.add(Pair.of(pos, "Nether Fortress"));
            }
        }

        return visits;
    }

	/** Put all child parts of the fortress on the map as global custom tiles. */
	private void visitFortress(World world, CompoundNBT tag) {
		ListNBT children = tag.getList("Children", 10);
		for (int i = 0; i < children.size(); i++) {
			CompoundNBT child = children.getCompound(i);
			String childID = child.getString("id");
			MutableBoundingBox boundingBox = new MutableBoundingBox(child.getIntArray("BB"));
			if (BRIDGE.equals(childID)) { // Straight open bridge segment. Is allowed to span several chunks.
				if (boundingBox.getXSize() > 16) {
					ResourceLocation tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_X;

					int chunkZ = MathUtil.getCenter(boundingBox).getZ() >> 4;
					for (int x = boundingBox.minX; x < boundingBox.maxX; x += 16) {
						int chunkX = x >> 4;
						if (noTileAt(world, chunkX, chunkZ)) {
							AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
						}
					}
				} else {//if (boundingBox.getZSize() > 16) {
					ResourceLocation tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_Z;

					int chunkX = MathUtil.getCenter(boundingBox).getX() >> 4;
					for (int z = boundingBox.minZ; z < boundingBox.maxZ; z += 16) {
						int chunkZ = z >> 4;
						if (noTileAt(world, chunkX, chunkZ)) {
							AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
						}
					}
				}
			} else if (BRIDGE_END.equals(childID)) { // End of a straight open bridge segment
				ResourceLocation tileName;
				int chunkX, chunkZ;
				if (boundingBox.getXSize() > boundingBox.getZSize()) {
					tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_END_X;
					chunkX = boundingBox.minX >> 4;
					chunkZ = MathUtil.getCenter(boundingBox).getZ() >> 4;
				} else {
					tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_END_Z;
					chunkX = MathUtil.getCenter(boundingBox).getX() >> 4;
					chunkZ = boundingBox.minZ >> 4;
				}
				if (noTileAt(world, chunkX, chunkZ)) {
					AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				}
			} else {
				int chunkX = MathUtil.getCenter(boundingBox).getX() >> 4;
				int chunkZ = MathUtil.getCenter(boundingBox).getZ() >> 4;
				ResourceLocation tileName;
				if (BRIDGE_GATE.equals(childID)) {
					tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_GATE;
					AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				} else if (BRIDGE_CROSS.equals(childID) || START.equals(childID)) {
					tileName = ExtTileIdMap.TILE_NETHER_BRIDGE;
					AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				} else if (TOWER.equals(childID)) {
					tileName = ExtTileIdMap.TILE_NETHER_TOWER;
					AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				} else if (ENTRANCE.equals(childID)) {
					tileName = ExtTileIdMap.TILE_NETHER_HALL;
					AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				} else if (WART_STAIRS.equals(childID)) {
					tileName = ExtTileIdMap.TILE_NETHER_FORT_STAIRS;
					AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				} else if (THRONE.equals(childID)) {
					tileName = ExtTileIdMap.TILE_NETHER_THRONE;
					AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				} else {
					tileName = ExtTileIdMap.TILE_NETHER_WALL;
					if (noTileAt(world, chunkX, chunkZ)) {
						AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
					}
				}
			}
		}
	}

	private static boolean noTileAt(World world, int chunkX, int chunkZ) {
		return AntiqueAtlasMod.extBiomeData.getData().getBiomeAt(world.dimension.getType(), chunkX, chunkZ) == -1;
	}
}
