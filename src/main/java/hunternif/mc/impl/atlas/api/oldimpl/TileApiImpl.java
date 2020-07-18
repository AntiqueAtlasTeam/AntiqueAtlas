package hunternif.mc.impl.atlas.api.oldimpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hunternif.mc.impl.atlas.ext.TileDataStorage;
import hunternif.mc.impl.atlas.ext.TileIdRegisteredCallback;
import hunternif.mc.impl.atlas.network.packet.c2s.play.PutTileC2SPacket;
import hunternif.mc.impl.atlas.network.packet.c2s.play.RegisterTileC2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.DeleteCustomGlobalTileS2CPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.PutTileS2CPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.CustomTileInfoS2CPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.TileNameS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.TileAPI;
import hunternif.mc.impl.atlas.client.BiomeTextureMap;
import hunternif.mc.impl.atlas.client.TextureSet;
import hunternif.mc.impl.atlas.client.TextureSetMap;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.ext.ExtTileTextureMap;
import hunternif.mc.impl.atlas.util.Log;

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

	private void putTile(World world, int atlasID, Identifier kind, int chunkX, int chunkZ) {
		RegistryKey<World> dimension = world.getRegistryKey();
		if (world.isClient) {
			new PutTileC2SPacket(atlasID, chunkX, chunkZ, kind).send();
		} else {
			AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
			data.setTile(dimension, chunkX, chunkZ, kind);
			for (PlayerEntity syncedPlayer : data.getSyncedPlayers()) {
				new PutTileS2CPacket(atlasID, dimension, chunkX, chunkZ, kind).send((ServerPlayerEntity) syncedPlayer);
			}
		}
	}

	@Override
	public void putBiomeTile(World world, int atlasID, Identifier biomeId, int chunkX, int chunkZ) {
		putTile(world, atlasID, biomeId, chunkX, chunkZ);
	}
	
	
	// Custom tiles ============================================================
	
	@Override
	public void putCustomTile(World world, int atlasID, Identifier tileId, int chunkX, int chunkZ) {
		if (tileId == null) {
			Log.error("Attempted to put custom tile with null name");
			return;
		}

		putTile(world, atlasID, tileId, chunkX, chunkZ);
	}
	
	@Override
	public void putCustomGlobalTile(World world, Identifier tileId, int chunkX, int chunkZ) {
		if (tileId == null) {
			Log.error("Attempted to put custom global tile with null name");
			return;
		}

		if (world.isClient) {
			Log.warn("Client attempted to put global tile");
			return;
		}

		TileDataStorage data = AntiqueAtlasMod.tileData.getData();
		data.setTile(world.getRegistryKey(), chunkX, chunkZ, tileId);

		// Send tile packet:
		new CustomTileInfoS2CPacket(world.getRegistryKey(), chunkX, chunkZ, tileId).send(world.getServer());
	}
	
	public void onTileIdRegistered(Collection<Identifier> ids) {
		for (Identifier id : ids) {
			// Put pending tiles:
			TileData tile = pendingTiles.remove(id);
			if (tile != null) {
				putBiomeTile(tile.world, tile.atlasID, id, tile.x, tile.z);
			}
		}
	}


	@Override
	public void deleteCustomGlobalTile(World world, int chunkX, int chunkZ) {
		if (world.isClient) {
			Log.warn("Client attempted to delete global tile");
			return;
		}
		TileDataStorage data = AntiqueAtlasMod.tileData.getData();
		RegistryKey<World> worldKey = world.getRegistryKey();
		if (data.getTile(worldKey, chunkX, chunkZ) != null) {
			data.removeTile(worldKey, chunkX, chunkZ);
			new DeleteCustomGlobalTileS2CPacket(worldKey, chunkX, chunkZ).send(world.getServer());
		}
	}
}
