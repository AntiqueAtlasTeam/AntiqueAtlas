package hunternif.mc.atlas.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import hunternif.mc.atlas.core.TileKind;
import hunternif.mc.atlas.core.TileKindFactory;

import hunternif.mc.atlas.ext.TileIdRegisteredEvent;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.bidirectional.PutTilePacket;
import hunternif.mc.atlas.network.client.DeleteCustomGlobalTilePacket;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import hunternif.mc.atlas.network.client.TilesPacket;
import hunternif.mc.atlas.network.server.RegisterTileIdPacket;
import hunternif.mc.atlas.util.Log;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

public class TileApiImpl implements TileAPI {
	/**
	 * When a tile is being put in an atlas on the client, the pseudo-biome ID
	 * may have been unregistered yet at that moment. In that case the tile data
	 * is put into this map to be later registered when the server sends the
	 * packet with the pseudo-biome ID for the corresponding unique name.
	 */
	private final Map<ResourceLocation, TileData> pendingTiles = new HashMap<>();
	private static class TileData {
		final World world;
		final int atlasID, x, z;
		TileData(World world, int atlasID, int x, int z) {
			this.world = world;
			this.atlasID = atlasID;
			this.x = x;
			this.z = z;
		}
	}
	
	public TileApiImpl() {
		//TileIdRegisteredEvent.EVENT.register(this::onTileIdRegistered);
	}
	
	
	@Override
	public TextureSet registerTextureSet(ResourceLocation name, ResourceLocation... textures) {
		TextureSet textureSet = new TextureSet(name, textures);
		TextureSetMap.instance().register(textureSet);
		return textureSet;
	}
	
	
	// Biome textures ==========================================================

	@Override
	public void setBiomeTexture(Biome biome, ResourceLocation textureSetName, ResourceLocation... textures) {
		TextureSet set = new TextureSet(textureSetName, textures);
		TextureSetMap.instance().register(set);
		BiomeTextureMap.instance().setTexture(biome, set);
	}

	@Override
	public void setBiomeTexture(Biome biome, TextureSet textureSet) {
		BiomeTextureMap.instance().setTexture(biome, textureSet);
	}
	
	
	// Custom tile textures ====================================================
	
	@Override
	public void setCustomTileTexture(ResourceLocation uniqueTileName, ResourceLocation ... textures) {
		TextureSet set = new TextureSet(uniqueTileName, textures);
		TextureSetMap.instance().register(set);
		setCustomTileTexture(uniqueTileName, set);
	}
	
	@Override
	public void setCustomTileTexture(ResourceLocation uniqueTileName, TextureSet textureSet) {
		ExtTileTextureMap.instance().setTexture(uniqueTileName, textureSet);
	}
	
	
	// Biome tiles =============================================================

	private void putTile(World world, int atlasID, TileKind kind, int chunkX, int chunkZ) {
		DimensionType dimension = world.dimension.getType();
		PutTilePacket packet = new PutTilePacket(atlasID, dimension, chunkX, chunkZ, kind);
		if (world.isRemote) {
			PacketDispatcher.INSTANCE.sendToServer(packet);
		} else {
			AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
			data.setTile(dimension, chunkX, chunkZ, kind);
			for (PlayerEntity syncedPlayer : data.getSyncedPlayers()) {
				PacketDispatcher.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) syncedPlayer), new PutTilePacket(atlasID, dimension, chunkX, chunkZ, kind));
			}
		}
	}

	@Override
	public void putBiomeTile(World world, int atlasID, Biome biome, int chunkX, int chunkZ) {
		putTile(world, atlasID, TileKindFactory.get(biome), chunkX, chunkZ);
	}
	
	
	// Custom tiles ============================================================
	
	@Override
	public void putCustomTile(World world, int atlasID, ResourceLocation tileName, int chunkX, int chunkZ) {
		if (tileName == null) {
			Log.error("Attempted to put custom tile with null name");
			return;
		}
		if (world.isRemote) {
			int biomeID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
			if (biomeID != ExtTileIdMap.NOT_FOUND) {
				putTile(world, atlasID, TileKindFactory.get(tileName), chunkX, chunkZ);
			} else {
				pendingTiles.put(tileName, new TileData(world, atlasID, chunkX, chunkZ));
				PacketDispatcher.INSTANCE.sendToServer(new RegisterTileIdPacket(tileName));
			}
		} else {
			int biomeID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
			if (biomeID == ExtTileIdMap.NOT_FOUND) {
				biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
				TileNameIDPacket packet = new TileNameIDPacket();
				packet.put(tileName, biomeID);
				PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
			}
			putTile(world, atlasID, TileKindFactory.get(biomeID), chunkX, chunkZ);
		}
	}
	
	@Override
	public void putCustomGlobalTile(World world, ResourceLocation tileName, int chunkX, int chunkZ) {
		if (tileName == null) {
			Log.error("Attempted to put custom global tile with null name");
			return;
		}
		if (world.isRemote) {
			Log.warn("Client attempted to put global tile");
			return;
		}
		boolean isIdRegistered = ExtTileIdMap.instance().getPseudoBiomeID(tileName) != ExtTileIdMap.NOT_FOUND;
		int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		data.setBiomeAt(world.dimension.getType(), chunkX, chunkZ, biomeID);
		// Send name-ID packet:
		if (!isIdRegistered) {
			TileNameIDPacket packet = new TileNameIDPacket();
			packet.put(tileName, biomeID);
			PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
		}
		// Send tile packet:
		TilesPacket packet = new TilesPacket(world.dimension.getType());
		packet.addTile(chunkX, chunkZ, biomeID);
		PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
	}
	
	public void onTileIdRegistered(Map<ResourceLocation, Integer> nameToIdMap) {
		for (Entry<ResourceLocation, Integer> entry : nameToIdMap.entrySet()) {
			// Put pending tiles:
			TileData tile = pendingTiles.remove(entry.getKey());
			if (tile != null) {
				putBiomeTile(tile.world, tile.atlasID, TileKindFactory.get(entry.getValue()).getBiome(), tile.x, tile.z);
			}
		}
	}


	@Override
	public void deleteCustomGlobalTile(World world, int chunkX, int chunkZ) {
		if (world.isRemote) {
			Log.warn("Client attempted to delete global tile");
			return;
		}
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		DimensionType dimension = world.dimension.getType();
		if (data.getBiomeAt(dimension, chunkX, chunkZ) != -1) {
			data.removeBiomeAt(dimension, chunkX, chunkZ);
			PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), new DeleteCustomGlobalTilePacket(dimension, chunkX, chunkZ));
		}
	}
}
