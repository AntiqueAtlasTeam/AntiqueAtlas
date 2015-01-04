package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.MapTileStitcher;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.util.ByteUtil;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAtlas extends Item {
	protected static final String ATLAS_DATA_PREFIX = "aAtlas_";
	protected static final String WORLD_ATLAS_DATA_ID = "aAtlas";
	protected static final String MARKERS_DATA_PREFIX = "aaMarkers_";
	
	/** In [chunks] */
	public static double LOOK_RADIUS = 11;
	/** In [ticks] */
	public static int UPDATE_INTERVAL = 20;
	
	private ChunkBiomeAnalyzer biomeAnalyzer;

	public ItemAtlas() {
		setHasSubtypes(true);
		setMaxStackSize(1);
	}
	
	public void setBiomeAnalyzer(ChunkBiomeAnalyzer biomeAnalyzer) {
		this.biomeAnalyzer = biomeAnalyzer;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(AntiqueAtlasMod.ID + ":" + getUnlocalizedName().substring("item.".length()));
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			AntiqueAtlasMod.proxy.openAtlasGUI(stack);
		}
		return stack;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isEquipped) {
		AtlasData data = getAtlasData(stack, world);
		if (data == null || !(entity instanceof EntityPlayer)) return;
		
		// On the first run send the map from the server to the client:
		EntityPlayer player = (EntityPlayer) entity;
		if (!world.isRemote && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
			data.syncOnPlayer(stack.getItemDamage(), player);
		}
		
		// Same thing with the local markers:
		MarkersData markers = getMarkersData(stack, world);
		if (!world.isRemote && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
			markers.syncOnPlayer(stack.getItemDamage(), player);
		}
		
		// Update the actual map only so often:
		if (player.ticksExisted % UPDATE_INTERVAL != 0 || biomeAnalyzer == null) {
			return;
		}
		
		int playerX = MathHelper.floor_double(player.posX) >> 4;
		int playerZ = MathHelper.floor_double(player.posZ) >> 4;
		Map<ShortVec2, MapTile> seenChunks = data.getSeenChunksInDimension(player.dimension);
		
		// Look at chunks around in a circular area:
		ShortVec2 coords = new ShortVec2(0, 0);
		for (double dx = -LOOK_RADIUS; dx <= LOOK_RADIUS; dx++) {
			for (double dz = -LOOK_RADIUS; dz <= LOOK_RADIUS; dz++) {
				if (dx*dx + dz*dz > LOOK_RADIUS*LOOK_RADIUS) {
					continue; // Outside the circle
				}
				coords.x = (short)(playerX + dx);
				coords.y = (short)(playerZ + dz);
				
				// Check if there's a custom tile at the location:
				int biomeId = AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(player.dimension, coords);
				// Custom tiles overwrite even the chunks already seen.
				
				// If there's no custom tile, check the actual chunk:
				if (biomeId == ChunkBiomeAnalyzer.NOT_FOUND) {
					// Check if the chunk has been seen already:
					if (seenChunks.containsKey(coords)) continue;
					
					// Check if the chunk has been loaded:
					if (!player.worldObj.blockExists((int)coords.x << 4, 0, (int)coords.y << 4)) {
						continue;
					}
					// Retrieve mean chunk biome and store it in AtlasData:
					Chunk chunk = player.worldObj.getChunkFromChunkCoords(coords.x, coords.y);
					if (!chunk.isChunkLoaded) {
						biomeId = ChunkBiomeAnalyzer.NOT_FOUND;
					} else {
						biomeId = biomeAnalyzer.getMeanBiomeID(
								ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray()));
					}
					// Finally, put the tile in place:
					if (biomeId != ChunkBiomeAnalyzer.NOT_FOUND) {
						MapTile tile = new MapTile(biomeId);
						if (world.isRemote) {
							tile.randomizeTexture();
						}
						data.putTile(player.dimension, coords.clone(), tile);
						if (!world.isRemote) {
							data.markDirty();
						}
					}
				} else {
					// Only update the custom tile if it doesn't rewrite itself:
					MapTile oldTile = seenChunks.get(coords);
					if (oldTile == null || oldTile.biomeID != biomeId) {
						MapTile tile = new MapTile(biomeId);
						if (world.isRemote) {
							tile.randomizeTexture();
						}
						data.putTile(player.dimension, coords.clone(), tile);
						if (!world.isRemote) {
							data.markDirty();
						}
					}
				}
				
			}
		}
	}
	
	// ====================== Obtaining AtlasData ======================
	
	public AtlasData getAtlasData(ItemStack stack, World world) {
		String key = getAtlasDataKey(stack);
		AtlasData data = (AtlasData) world.loadItemData(AtlasData.class, key);
		if (data == null && !world.isRemote) {
			// This shouldn't really happen
			stack.setItemDamage(world.getUniqueDataId(WORLD_ATLAS_DATA_ID));
			key = getAtlasDataKey(stack);
			data = new AtlasData(key);
			world.setItemData(key, data);
			if (world.isRemote) {
				data.setTileStitcher(MapTileStitcher.instance);
			}
		}
		return data;
	}
	
	/** Used to update data from the server. */
	@SideOnly(Side.CLIENT)
	public AtlasData getClientAtlasData(int atlasID) {
		String key = getAtlasDataKey(atlasID);
		World world = Minecraft.getMinecraft().theWorld;
		AtlasData data = (AtlasData) world.loadItemData(AtlasData.class, key);
		if (data == null) {
			data = new AtlasData(key);
			data.setTileStitcher(MapTileStitcher.instance);
			world.setItemData(key, data);
		}
		return data;
	}
	
	protected String getAtlasDataKey(ItemStack stack) {
		return getAtlasDataKey(stack.getItemDamage());
	}
	protected String getAtlasDataKey(int atlasID) {
		return ATLAS_DATA_PREFIX + atlasID;
	}
	
	// ====================== Obtaining MarkersData ======================
	
	public MarkersData getMarkersData(ItemStack stack, World world) {
		String key = getMarkersDataKey(stack);
		MarkersData data = (MarkersData) world.loadItemData(MarkersData.class, key);
		if (data == null && !world.isRemote) {
			// Biome data defines the ID; Markers data is secondary and has to cope.
			data = new MarkersData(key);
			world.setItemData(key, data);
		}
		return data;
	}
	
	/** Returns null if such atlasID was not found. */
	public MarkersData getMarkersData(int atlasID, World world) {
		String key = getMarkersDataKey(atlasID);
		MarkersData data = (MarkersData) world.loadItemData(MarkersData.class, key);
		return data;
	}
	
	/** Used to update data from the server. */
	@SideOnly(Side.CLIENT)
	public MarkersData getClientMarkersData(int atlasID) {
		String key = getMarkersDataKey(atlasID);
		World world = Minecraft.getMinecraft().theWorld;
		MarkersData data = (MarkersData) world.loadItemData(MarkersData.class, key);
		if (data == null) {
			data = new MarkersData(key);
			world.setItemData(key, data);
		}
		return data;
	}
	
	protected String getMarkersDataKey(ItemStack stack) {
		return getMarkersDataKey(stack.getItemDamage());
	}
	protected String getMarkersDataKey(int atlasID) {
		return MARKERS_DATA_PREFIX + atlasID;
	}

}
