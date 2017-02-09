package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.util.Log;

import java.util.HashSet;
import java.util.Set;

import hunternif.mc.atlas.util.MathUtil;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class NetherFortressWatcher {
	/** Set of tag names for every fortress, in the format "[x, y]" */
	private final Set<String> visited = new HashSet<String>();
	
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
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == -1) {
			visitAllUnvisitedFortresses(event.getWorld());
		}
	}
	
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == -1) {
			visitAllUnvisitedFortresses(event.getWorld());
		}
	}
	
	public void visitAllUnvisitedFortresses(World world) {
		MapGenStructureData data = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, "Fortress");
		if (data == null) return;
		NBTTagCompound fortressNBTData = data.getTagCompound();
		Set<String> tagSet = fortressNBTData.getKeySet();
		for (String coords : tagSet) {
			if (!visited.contains(coords)) {
				NBTBase tag = fortressNBTData.getTag(coords);
				if (tag.getId() == 10) { // is NBTTagCompound
					visitFortress(world, (NBTTagCompound) tag);
					visited.add(coords);
				}
			}
		}
	}
	
	/** Put all child parts of the fortress on the map as global custom tiles. */
	private void visitFortress(World world, NBTTagCompound tag) {
		int startChunkX = tag.getInteger("ChunkX");
		int startChunkZ = tag.getInteger("ChunkZ");
		Log.info("Visiting Nether Fortress in dimension #%d \"%s\" at chunk (%d, %d) ~ blocks (%d, %d)",
				world.provider.getDimension(), world.provider.getDimensionType().getName(),
				startChunkX, startChunkZ, startChunkX << 4, startChunkZ << 4);
		NBTTagList children = tag.getTagList("Children", 10);
		for (int i = 0; i < children.tagCount(); i++) {
			NBTTagCompound child = children.getCompoundTagAt(i);
			String childID = child.getString("id");
			StructureBoundingBox boundingBox = new StructureBoundingBox(child.getIntArray("BB"));
			if (BRIDGE.equals(childID)) { // Straight open bridge segment. Is allowed to span several chunks.
				if (boundingBox.getXSize() > 16) {
					String tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_X;
					int chunkZ = MathUtil.getCenter(boundingBox).getZ() >> 4;
					for (int x = boundingBox.minX; x < boundingBox.maxX; x += 16) {
						int chunkX = x >> 4;
						if (noTileAt(world, chunkX, chunkZ)) {
							AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
						}
					}
				} else {//if (boundingBox.getZSize() > 16) {
					String tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_Z;
					int chunkX = MathUtil.getCenter(boundingBox).getX() >> 4;
					for (int z = boundingBox.minZ; z < boundingBox.maxZ; z += 16) {
						int chunkZ = z >> 4;
						if (noTileAt(world, chunkX, chunkZ)) {
							AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
						}
					}
				}
			} else if (BRIDGE_END.equals(childID)) { // End of a straight open bridge segment
				String tileName;
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
				String tileName;
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
		return AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(world.provider.getDimension(), chunkX, chunkZ) == -1;
	}
}
