package hunternif.mc.atlas.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.BiomeDetectorBase;
import hunternif.mc.atlas.core.BiomeDetectorEnd;
import hunternif.mc.atlas.core.BiomeDetectorNether;
import hunternif.mc.atlas.core.IBiomeDetector;
import hunternif.mc.atlas.core.ITileStorage;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.marker.MarkersData;

public class ItemAtlas extends Item {
	protected static final String WORLD_ATLAS_DATA_ID = "aAtlas";
	
	/** Maps dimension ID to biomeAnalyzer. */
	private final Map<Integer, IBiomeDetector> biomeAnalyzers = new HashMap<>();
	private final BiomeDetectorBase biomeDetectorOverworld = new BiomeDetectorBase();
	private final BiomeDetectorNether biomeDetectorNether = new BiomeDetectorNether();
	private final BiomeDetectorEnd biomeDetectorEnd = new BiomeDetectorEnd();
	
	private final SettingsConfig settings;

	public ItemAtlas(SettingsConfig settings) {
		this.settings = settings;
		biomeDetectorOverworld.setScanPonds(settings.doScanPonds);
		setBiomeDetectorForDimension(0, biomeDetectorOverworld);
		setBiomeDetectorForDimension(-1, biomeDetectorNether);
		setBiomeDetectorForDimension(1, biomeDetectorEnd);
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
	public String getItemStackDisplayName(ItemStack stack) {
		return super.getItemStackDisplayName(stack) + " #" + stack.getItemDamage();
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn,
			EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (world.isRemote) {
			AntiqueAtlasMod.proxy.openAtlasGUI(stack);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isEquipped) {
		AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(stack, world);
		if (data == null || !(entity instanceof EntityPlayer)) return;
		
		// On the first run send the map from the server to the client:
		EntityPlayer player = (EntityPlayer) entity;
		if (!world.isRemote && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
			data.syncOnPlayer(stack.getItemDamage(), player);
		}
		
		// Same thing with the local markers:
		MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
		if (!world.isRemote && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
			markers.syncOnPlayer(stack.getItemDamage(), player);
		}
		
		// Update the actual map only so often:
		int newScanInterval = Math.round(settings.newScanInterval * 20);
		int rescanInterval = newScanInterval * settings.rescanRate;
		if (player.ticksExisted % newScanInterval != 0) {
			return;
		}
		
		int playerX = MathHelper.floor(player.posX) >> 4;
		int playerZ = MathHelper.floor(player.posZ) >> 4;
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
					Chunk chunk = player.getEntityWorld().getChunkFromChunkCoords(x, z);
					// Force loading of chunk, if required:
					if (settings.forceChunkLoading && !chunk.isLoaded()) {
						player.getEntityWorld().getChunkProvider().provideChunk(x << 4, z << 4);
					}
					// Skip chunk if it hasn't loaded yet:
					if (!chunk.isLoaded()) { 
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

}
