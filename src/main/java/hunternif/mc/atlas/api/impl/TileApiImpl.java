package hunternif.mc.atlas.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import hunternif.mc.atlas.core.TileKind;
import hunternif.mc.atlas.core.TileKindFactory;

import hunternif.mc.atlas.ext.TileIdRegisteredCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
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

public class TileApiImpl implements TileAPI {
	/**
	 * When a tile is being put in an atlas on the client, the pseudo-biome ID
	 * may have been unregistered yet at that moment. In that case the tile data
	 * is put into this map to be later registered when the server sends the
	 * packet with the pseudo-biome ID for the corresponding unique name.
	 */
	private final Map<Identifier, TileData> pendingTiles = new HashMap<>();
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
		TileIdRegisteredCallback.EVENT.register(this::onTileIdRegistered);
	}
	
	
	@Override
	@Environment(EnvType.CLIENT)
	public TextureSet registerTextureSet(Identifier name, Identifier... textures) {
		TextureSet textureSet = new TextureSet(name, textures);
		TextureSetMap.instance().register(textureSet);
		return textureSet;
	}
	
	
	// Biome textures ==========================================================

	@Override
	@Environment(EnvType.CLIENT)
	public void setBiomeTexture(Biome biome, Identifier textureSetName, Identifier... textures) {
		TextureSet set = new TextureSet(textureSetName, textures);
		TextureSetMap.instance().register(set);
		BiomeTextureMap.instance().setTexture(biome, set);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void setBiomeTexture(Biome biome, TextureSet textureSet) {
		BiomeTextureMap.instance().setTexture(biome, textureSet);
	}
	
	
	// Custom tile textures ====================================================
	
	@Override
	@Environment(EnvType.CLIENT)
	public void setCustomTileTexture(Identifier uniqueTileName, Identifier ... textures) {
		TextureSet set = new TextureSet(uniqueTileName, textures);
		TextureSetMap.instance().register(set);
		setCustomTileTexture(uniqueTileName, set);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void setCustomTileTexture(Identifier uniqueTileName, TextureSet textureSet) {
		ExtTileTextureMap.instance().setTexture(uniqueTileName, textureSet);
	}
	
	
	// Biome tiles =============================================================

	private void putTile(World world, int atlasID, TileKind kind, int chunkX, int chunkZ) {
		RegistryKey<DimensionType> dimension = world.getDimensionRegistryKey();
		PutTilePacket packet = new PutTilePacket(atlasID, dimension, chunkX, chunkZ, kind);
		if (world.isClient) {
			PacketDispatcher.sendToServer(packet);
		} else {
			AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
			data.setTile(dimension, chunkX, chunkZ, kind);
			for (PlayerEntity syncedPlayer : data.getSyncedPlayers()) {
				PacketDispatcher.sendTo(new PutTilePacket(atlasID, dimension, chunkX, chunkZ, kind), (ServerPlayerEntity) syncedPlayer);
			}
		}
	}

	@Override
	public void putBiomeTile(World world, int atlasID, Biome biome, int chunkX, int chunkZ) {
		putTile(world, atlasID, TileKindFactory.get(biome), chunkX, chunkZ);
	}
	
	
	// Custom tiles ============================================================
	
	@Override
	public void putCustomTile(World world, int atlasID, Identifier tileName, int chunkX, int chunkZ) {
		if (tileName == null) {
			Log.error("Attempted to put custom tile with null name");
			return;
		}
		if (world.isClient) {
			int biomeID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
			if (biomeID != ExtTileIdMap.NOT_FOUND) {
				putTile(world, atlasID, TileKindFactory.get(tileName), chunkX, chunkZ);
			} else {
				pendingTiles.put(tileName, new TileData(world, atlasID, chunkX, chunkZ));
				PacketDispatcher.sendToServer(new RegisterTileIdPacket(tileName));
			}
		} else {
			int biomeID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
			if (biomeID == ExtTileIdMap.NOT_FOUND) {
				biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
				TileNameIDPacket packet = new TileNameIDPacket();
				packet.put(tileName, biomeID);
				PacketDispatcher.sendToAll(((ServerWorld) world).getServer(), packet);
			}
			putTile(world, atlasID, TileKindFactory.get(biomeID), chunkX, chunkZ);
		}
	}
	
	@Override
	public void putCustomGlobalTile(World world, Identifier tileName, int chunkX, int chunkZ) {
		if (tileName == null) {
			Log.error("Attempted to put custom global tile with null name");
			return;
		}
		if (world.isClient) {
			Log.warn("Client attempted to put global tile");
			return;
		}
		boolean isIdRegistered = ExtTileIdMap.instance().getPseudoBiomeID(tileName) != ExtTileIdMap.NOT_FOUND;
		int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		data.setBiomeAt(world.getDimensionRegistryKey(), chunkX, chunkZ, biomeID);
		// Send name-ID packet:
		if (!isIdRegistered) {
			TileNameIDPacket packet = new TileNameIDPacket();
			packet.put(tileName, biomeID);
			PacketDispatcher.sendToAll(((ServerWorld) world).getServer(), packet);
		}
		// Send tile packet:
		TilesPacket packet = new TilesPacket(world.getDimensionRegistryKey());
		packet.addTile(chunkX, chunkZ, biomeID);
		PacketDispatcher.sendToAll(((ServerWorld) world).getServer(), packet);
	}
	
	public void onTileIdRegistered(Map<Identifier, Integer> nameToIdMap) {
		for (Entry<Identifier, Integer> entry : nameToIdMap.entrySet()) {
			// Put pending tiles:
			TileData tile = pendingTiles.remove(entry.getKey());
			if (tile != null) {
				putBiomeTile(tile.world, tile.atlasID, TileKindFactory.get(entry.getValue()).getBiome(), tile.x, tile.z);
			}
		}
	}


	@Override
	public void deleteCustomGlobalTile(World world, int chunkX, int chunkZ) {
		if (world.isClient) {
			Log.warn("Client attempted to delete global tile");
			return;
		}
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		RegistryKey<DimensionType> dimension = world.getDimensionRegistryKey();
		if (data.getBiomeAt(dimension, chunkX, chunkZ) != -1) {
			data.removeBiomeAt(dimension, chunkX, chunkZ);
			PacketDispatcher.sendToAll(((ServerWorld) world).getServer(), new DeleteCustomGlobalTilePacket(dimension, chunkX, chunkZ));
		}
	}
}
