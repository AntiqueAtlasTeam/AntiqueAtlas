package hunternif.mc.impl.atlas.api.impl;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.TileAPI;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.packet.c2s.play.PutTileC2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.CustomTileInfoS2CPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.DeleteCustomGlobalTileS2CPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.PutTileS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TileApiImpl implements TileAPI {
	public TileApiImpl() {
	}
	
	public void putTile(World world, int atlasID, ResourceLocation tile, int chunkX, int chunkZ) {
        if (tile == null) {
            Log.error("Attempted to put custom tile with null name");
            return;
    }
	
		RegistryKey<World> dimension = world.getDimensionKey();
		if (world.isRemote) {
			new PutTileC2SPacket(atlasID, chunkX, chunkZ, tile).send();
		} else {
			AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, world);
			data.setTile(dimension, chunkX, chunkZ, tile);
			for (PlayerEntity syncedPlayer : data.getSyncedPlayers()) {
				new PutTileS2CPacket(atlasID, dimension, chunkX, chunkZ, tile).send((ServerPlayerEntity) syncedPlayer);
			}
		}
	}

	@Override
	public ResourceLocation getTile(World world, int atlasID, int chunkX, int chunkZ) {
        AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, world);
        return data.getWorldData(world.getDimensionKey()).getTile(chunkX, chunkZ);
	}
	
	@Override
	public void putGlobalTile(World world, ResourceLocation tileId, int chunkX, int chunkZ) {
		if (tileId == null) {
			Log.error("Attempted to put global tile with null name");
			return;
		}

		if (world.isRemote) {
			Log.warn("Client attempted to put global tile");
			return;
		}

		TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
		data.setTile(chunkX, chunkZ, tileId);

		// Send tile packet:
		new CustomTileInfoS2CPacket(world.getDimensionKey(), chunkX, chunkZ, tileId).send((ServerWorld) world);
	}
	
	@Override
    public ResourceLocation getGlobalTile(World world, int chunkX, int chunkZ) {
        TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
        return data.getTile(chunkX, chunkZ);
	}


	@Override
	public void deleteGlobalTile(World world, int chunkX, int chunkZ) {
		if (world.isRemote) {
			Log.warn("Client attempted to delete global tile");
			return;
		}
		TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
		if (data.getTile(chunkX, chunkZ) != null) {
			data.removeTile(chunkX, chunkZ);
			new DeleteCustomGlobalTileS2CPacket(world.getDimensionKey(), chunkX, chunkZ).send((ServerWorld) world);
		}
	}
}
