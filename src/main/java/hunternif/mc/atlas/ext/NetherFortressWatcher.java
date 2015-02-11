package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class NetherFortressWatcher {
	/** Set of tag names for every fortress, in the format "[x, y]" */
	private final Set<String> visited = new HashSet<String>();
	
	/* ========= Parts of the fortress: ===========
	// Corridors:
	"NeSCLT" - Roofed corridor, solid wall down to the ground
	"NeSCR" - Another roofed corridor? i guess
	"NeCCS" - Roofed stairs, solid wall down to the ground
	"NeCTB" - Really small roofed corridor
	"NeSC" - ? Roofed? Covers most of the area of the Fortress. Solid wall down to the ground?
	
	// Crossings:
	"NeRC" - That room with no roof with gates facing each direction. One thick solid column going down to the ground. 
	"NeSCSC" - Roofed corridor?
	"NeBCr" - A crossing of open bridges. No roof, no column. Takes up 19x19 area because of the beginnings of bridges starting off in different directions.
	"NeStart" - The same as "NeBCr"
	
	// Bridges:
	"NeBS - "19-block-long section of the bridge with columns, no roof.
	"NeBEF - "The ruined end of a bridge
	
	"NeCE" - "Entrance", a room with an iron-barred gate. Contains a well of lava in the center.
	"NeCSR" - Room with the Nether Wart and a wide staircase leading to an open roof with a fence railing.
	"NeMT" - Blaze spawner. No roof. A decorative wall of fence ("the throne"?)
	"NeSR" - That room with tiny stairs going up to the roof along the wall
	*/
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.world.isRemote && event.world.provider.dimensionId == -1) {
			visitAllUnvisitedFortresses(event.world);
		}
	}
	
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		if (!event.world.isRemote && event.world.provider.dimensionId == -1) {
			visitAllUnvisitedFortresses(event.world);
		}
	}
	
	public void visitAllUnvisitedFortresses(World world) {
		MapGenStructureData data = (MapGenStructureData)world.perWorldStorage.loadData(MapGenStructureData.class, "Fortress");
		if (data == null) return;
		NBTTagCompound fortressNBTData = data.func_143041_a();
		@SuppressWarnings("unchecked")
		Set<String> tagSet = fortressNBTData.func_150296_c();
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
		NBTTagList children = tag.getTagList("Children", 10);
		for (int i = 0; i < children.tagCount(); i++) {
			NBTTagCompound child = children.getCompoundTagAt(i);
			String childID = child.getString("id");
			StructureBoundingBox boundingBox = new StructureBoundingBox(child.getIntArray("BB"));
			if ("NeBS".equals(childID)) { // Straight open bridge segment. Is allowed to span several chunks.
				if (boundingBox.getXSize() > 16) {
					String tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_X;
					int chunkZ = boundingBox.getCenterZ() >> 4;
					for (int x = boundingBox.minX; x < boundingBox.maxX; x += 16) {
						int chunkX = x >> 4;
						if (noTileAt(chunkX, chunkZ)) {
							AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
						}
					}
				} else {//if (boundingBox.getZSize() > 16) {
					String tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_Z;
					int chunkX = boundingBox.getCenterX() >> 4;
					for (int z = boundingBox.minZ; z < boundingBox.maxZ; z += 16) {
						int chunkZ = z >> 4;
						if (noTileAt(chunkX, chunkZ)) {
							AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
						}
					}
				}
			} else if ("NeBEF".equals(childID)) { // End of a straight open bridge segment
				String tileName;
				int chunkX, chunkZ;
				if (boundingBox.getXSize() > boundingBox.getZSize()) {
					tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_END_X;
					chunkX = boundingBox.minX >> 4;
					chunkZ = boundingBox.getCenterZ() >> 4;
				} else {
					tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_END_Z;
					chunkX = boundingBox.getCenterX() >> 4;
					chunkZ = boundingBox.minZ >> 4;
				}
				if (noTileAt(chunkX, chunkZ)) {
					AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
				}
			} else {
				String tileName = ExtTileIdMap.TILE_NETHER_BRIDGE; // TODO: handle other parts of the fortress
				int chunkX = boundingBox.getCenterX() >> 4;
				int chunkZ = boundingBox.getCenterZ() >> 4;
				AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
			}
		}
	}
	
	private static boolean noTileAt(int chunkX, int chunkZ) {
		return AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(-1, chunkX, chunkZ) == -1;
	}
}
