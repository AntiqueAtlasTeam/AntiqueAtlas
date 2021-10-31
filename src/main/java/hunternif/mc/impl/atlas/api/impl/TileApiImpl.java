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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


public class TileApiImpl implements TileAPI {
    public TileApiImpl() {
    }

    public void putTile(Level world, int atlasID, ResourceLocation tile, int chunkX, int chunkZ) {
        if (tile == null) {
            Log.error("Attempted to put custom tile with null name");
            return;
        }

        ResourceKey<Level> dimension = world.dimension();
        if (world.isClientSide) {
            new PutTileC2SPacket(atlasID, chunkX, chunkZ, tile).send();
        } else {
            AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, world);
            data.setTile(dimension, chunkX, chunkZ, tile);
            for (Player syncedPlayer : data.getSyncedPlayers()) {
                new PutTileS2CPacket(atlasID, dimension, chunkX, chunkZ, tile).send((ServerPlayer) syncedPlayer);
            }
        }
    }

    @Override
    public ResourceLocation getTile(Level world, int atlasID, int chunkX, int chunkZ) {
        AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, world);

        return data.getWorldData(world.dimension()).getTile(chunkX, chunkZ);
    }

    @Override
    public void putGlobalTile(Level world, ResourceLocation tileId, int chunkX, int chunkZ) {
        if (tileId == null) {
            Log.error("Attempted to put global tile with null name");
            return;
        }

        if (world.isClientSide) {
            Log.warn("Client attempted to put global tile");
            return;
        }

        TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
        data.setTile(chunkX, chunkZ, tileId);

        // Send tile packet:
        new CustomTileInfoS2CPacket(world.dimension(), chunkX, chunkZ, tileId).send((ServerLevel) world);
    }

    @Override
    public ResourceLocation getGlobalTile(Level world, int chunkX, int chunkZ) {
        TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
        return data.getTile(chunkX, chunkZ);
    }

    @Override
    public void deleteGlobalTile(Level world, int chunkX, int chunkZ) {
        if (world.isClientSide) {
            Log.warn("Client attempted to delete global tile");
            return;
        }
        TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
        if (data.getTile(chunkX, chunkZ) != null) {
            data.removeTile(chunkX, chunkZ);
            new DeleteCustomGlobalTileS2CPacket(world.dimension(), chunkX, chunkZ).send((ServerLevel) world);
        }
    }
}
