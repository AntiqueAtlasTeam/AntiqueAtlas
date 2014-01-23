package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.MapTileStitcher;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;
import hunternif.mc.atlas.core.ChunkBiomeAnalyzer.BiomeFlag;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAtlas extends Item {
	protected static final String DATA_PREFIX = "aAtlas_";
	protected static final String WORLD_DATA_ID = "aAtlas";
	
	/** In [chunks] */
	public static double LOOK_RADIUS = 50;
	/** In [ticks] */
	public static int UPDATE_INTERVAL = 20;
	
	private ChunkBiomeAnalyzer biomeAnalyzer;

	public ItemAtlas(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxStackSize(1);
	}
	
	public void setBiomeAnalyzer(ChunkBiomeAnalyzer biomeAnalyzer) {
		this.biomeAnalyzer = biomeAnalyzer;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(AntiqueAtlasMod.ID + ":" + getUnlocalizedName().substring("item.".length()));
	}
	
	@Override
	public String getItemDisplayName(ItemStack stack) {
		return super.getItemDisplayName(stack) + " #" + stack.getItemDamage();
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			AntiqueAtlasMod.proxy.openAtlasGUI(stack);
		}
		return stack;
	}
	
	private boolean loaded = false;
	private int chunksLoaded = 0;
	
	private static final int SIDE_Y = 120;//157;
	private static final int SIDE_X = (int)(SIDE_Y*1.618);
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity player, int slot, boolean isEquipped) {
		AtlasData data = getAtlasData(stack, world);
		if (data == null) return;
		
		// On first run send the map from the server to the client:
		if (!world.isRemote && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
			data.syncOnPlayer(stack.getItemDamage(), player);
		}
		
		if (loaded || world.isRemote) return;
		
		// Update the actual map only so often:
		/*if (player.ticksExisted % UPDATE_INTERVAL != 0 || biomeAnalyzer == null) {
			return;
		}*/
		
		IChunkProvider chunkProvider = player.worldObj.getChunkProvider();
		ChunkProviderServer cp = null;
		if (chunkProvider instanceof ChunkProviderServer) cp = (ChunkProviderServer) chunkProvider;
		
		int playerX = MathHelper.floor_double(player.posX) >> 4;
		int playerZ = MathHelper.floor_double(player.posZ) >> 4;
		Map<ShortVec2, MapTile> seenChunks = data.getSeenChunksInDimension(player.dimension);
		
		// Look at chunks around in a circular area:
		ShortVec2 coords = new ShortVec2(0, 0);
		for (double dx = -SIDE_X; dx <= SIDE_X; dx++) {
			for (double dz = -SIDE_Y; dz <= SIDE_Y; dz++) {
				if (dz % 100 == 0) {
					System.out.println("Generating chunk: (" + dx + ", " + dz + ")");
				}
				/*if (dx*dx + dz*dz > LOOK_RADIUS*LOOK_RADIUS) {
					continue; // Outside the circle
				}*/
				coords.x = (short)(playerX + dx);
				coords.y = (short)(playerZ + dz);
				// Check if the chunk has been loaded:
				/*if (seenChunks.containsKey(coords) ||
						!player.worldObj.
						blockExists((int)coords.x << 4, 0, (int)coords.y << 4)) {
					continue;
				}*/
				// Retrieve mean chunk biome and store it in AtlasData:
				Chunk chunk = player.worldObj.getChunkFromChunkCoords(coords.x, coords.y);
				int meanBiomeId = biomeAnalyzer.getChunkBiomeID(chunk);
				if (meanBiomeId != BiomeFlag.NONE) {
					data.putTile(player.dimension, coords.copy(), new MapTile((byte)meanBiomeId));
					if (!world.isRemote && !data.isDirty()) {
						data.markDirty();
					}
				}
				chunksLoaded++;
				if (cp != null && chunksLoaded >= 100) {
					cp.unloadAllChunks();
					cp.unloadQueuedChunks();
					chunksLoaded = 0;
				}
			}
		}
		System.out.println("Finished generating world.");
		loaded = true;
	}
	
	public AtlasData getAtlasData(ItemStack stack, World world) {
		String key = getDataKey(stack);
		AtlasData data = (AtlasData) world.loadItemData(AtlasData.class, key);
		if (data == null && !world.isRemote) {
			// This shouldn't really happen
			stack.setItemDamage(world.getUniqueDataId(WORLD_DATA_ID));
			key = getDataKey(stack);
			data = new AtlasData(key);
			world.setItemData(key, data);
			if (world.isRemote) {
				data.setTileStitcher(MapTileStitcher.instance);
			}
		}
		return data;
	}
	
	@SideOnly(Side.CLIENT)
	public AtlasData getClientAtlasData(int atlasID) {
		String key = getDataKey(atlasID);
		World world = Minecraft.getMinecraft().theWorld;
		AtlasData data = (AtlasData) world.loadItemData(AtlasData.class, key);
		if (data == null) {
			data = new AtlasData(key);
			data.setTileStitcher(MapTileStitcher.instance);
			world.setItemData(key, data);
		}
		return data;
	}
	
	protected String getDataKey(ItemStack stack) {
		return getDataKey(stack.getItemDamage());
	}
	protected String getDataKey(int atlasID) {
		return DATA_PREFIX + atlasID;
	}

}
