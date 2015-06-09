package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.BiomeDetectorBase;
import hunternif.mc.atlas.core.BiomeDetectorNether;
import hunternif.mc.atlas.core.IBiomeDetector;
import hunternif.mc.atlas.core.ITileStorage;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.marker.MarkersData;

import java.util.HashMap;
import java.util.Map;

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
	
	/** Maps dimension ID to biomeAnalyzer. */
	private final Map<Integer, IBiomeDetector> biomeAnalyzers = new HashMap<Integer, IBiomeDetector>();
	private final BiomeDetectorBase biomeDetectorOverworld = new BiomeDetectorBase();
	private final BiomeDetectorNether biomeDetectorNether = new BiomeDetectorNether();
	
	private SettingsConfig settings;

	public ItemAtlas(SettingsConfig settings) {
		this.settings = settings;
		biomeDetectorOverworld.setScanPonds(settings.doScanPonds);
		setBiomeDetectorForDimension(0, biomeDetectorOverworld);
		setBiomeDetectorForDimension(-1, biomeDetectorNether);
		setHasSubtypes(true);
	}
	
	public void setBiomeDetectorForDimension(int dimension, IBiomeDetector biomeAnalyzer) {
		biomeAnalyzers.put(dimension, biomeAnalyzer);
	}
	/** If not found, returns the analyzer for overworld. */
	private IBiomeDetector getBiomeDetectorForDimension(int dimension) {
		IBiomeDetector biomeAnalyzer = biomeAnalyzers.get(dimension);
		return biomeAnalyzer == null ? biomeDetectorOverworld : biomeAnalyzer;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(AntiqueAtlasMod.ID + ":" + getUnlocalizedName().substring("item.".length()));
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return super.getItemStackDisplayName(stack) + " #" + stack.getItemDamage();
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
		markers = null;
		
		// Update the actual map only so often:
		int newScanInterval = Math.round(settings.newScanInterval * 20);
		int rescanInterval = newScanInterval * settings.rescanRate;
		if (player.ticksExisted % newScanInterval != 0) {
			return;
		}
		
		int playerX = MathHelper.floor_double(player.posX) >> 4;
		int playerZ = MathHelper.floor_double(player.posZ) >> 4;
		ITileStorage seenChunks = data.getDimensionData(player.dimension);
		IBiomeDetector biomeDetector = getBiomeDetectorForDimension(player.dimension);
		int scanRadius = settings.scanRadius;
		
		// Look at chunks around in a circular area:
		for (double dx = -scanRadius; dx <= scanRadius; dx++) {
			for (double dz = -scanRadius; dz <= scanRadius; dz++) {
				if (dx*dx + dz*dz > scanRadius*scanRadius) {
					continue; // Outside the circle
				}
				int x = (int)(playerX + dx);
				int z = (int)(playerZ + dz);
				Tile oldTile = seenChunks.getTile(x, z);
				
				// Check if there's a custom tile at the location:
				int biomeId = AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(player.dimension, x, z);
				// Custom tiles overwrite even the chunks already seen.
				
				// If there's no custom tile, check the actual chunk:
				if (biomeId == -1) {
					Chunk chunk = player.worldObj.getChunkFromChunkCoords(x, z);
					// Force loading of chunk, if required:
					if (settings.forceChunkLoading && !chunk.isChunkLoaded) {
						player.worldObj.getChunkProvider().loadChunk(x << 4, z << 4);
					}
					// Skip chunk if it hasn't loaded yet:
					if (!chunk.isChunkLoaded) { 
						continue;
					}
					
					if (oldTile != null) {
						// If the chunk has been scanned previously, only re-scan it so often:
						if (!settings.doRescan || player.ticksExisted % rescanInterval != 0) {
							continue;
						}
						biomeId = biomeDetector.getBiomeID(chunk);
						if (biomeId == IBiomeDetector.NOT_FOUND) {
							// If the new tile is empty, remove the old one:
							data.removeTile(player.dimension, x, z);
						} else if (oldTile.biomeID != biomeId) {
							// Only update if the old tile's biome ID doesn't match the new one:
							data.setTile(player.dimension, x, z, new Tile(biomeId));
						}
					} else {
						// Scanning new chunk:
						biomeId = biomeDetector.getBiomeID(chunk);
						if (biomeId != IBiomeDetector.NOT_FOUND) {
							data.setTile(player.dimension, x, z, new Tile(biomeId));
						}
					}
				} else {
					// Only update the custom tile if it doesn't rewrite itself:
					if (oldTile == null || oldTile.biomeID != biomeId) {
						data.setTile(player.dimension, x, z, new Tile(biomeId));
						data.markDirty();
					}
				}
				
			}
		}
	}
	
	// ====================== Obtaining AtlasData ======================
	
	/** Loads data for the given atlas ID or creates a new one. */
	public AtlasData getAtlasData(ItemStack stack, World world) {
		if (stack.getItem() == AntiqueAtlasMod.itemAtlas) {
			return getAtlasData(stack.getItemDamage(), world);
		} else {
			return null;
		}
	}
	
	/** Loads data for the given atlas or creates a new one. */
	public AtlasData getAtlasData(int atlasID, World world) {
		String key = getAtlasDataKey(atlasID);
		AtlasData data = (AtlasData) world.loadItemData(AtlasData.class, key);
		if (data == null) {
			data = new AtlasData(key);
			world.setItemData(key, data);
		}
		return data;
	}
	
	protected String getAtlasDataKey(int atlasID) {
		return ATLAS_DATA_PREFIX + atlasID;
	}
	
	
	// ====================== Obtaining MarkersData ======================
	
	/** Loads data for the given atlas or creates a new one. */
	public MarkersData getMarkersData(ItemStack stack, World world) {
		if (stack.getItem() == AntiqueAtlasMod.itemAtlas) {
			return getMarkersData(stack.getItemDamage(), world);
		} else {
			return null;
		}
	}
	
	/** Loads data for the given atlas ID or creates a new one. */
	public MarkersData getMarkersData(int atlasID, World world) {
		String key = getMarkersDataKey(atlasID);
		MarkersData data = (MarkersData) world.loadItemData(MarkersData.class, key);
		if (data == null) {
			data = new MarkersData(key);
			world.setItemData(key, data);
		}
		return data;
	}
	
	protected String getMarkersDataKey(int atlasID) {
		return MARKERS_DATA_PREFIX + atlasID;
	}

}
