package hunternif.mc.impl.atlas.api.client.impl;

import hunternif.mc.api.client.ClientTileAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.TileRenderIterator;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.packet.c2s.play.PutTileC2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.Rect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class TileApiImplClient implements ClientTileAPI {
    @Override
    public void putTile(World world, int atlasID, Identifier tile, int chunkX, int chunkZ) {
        new PutTileC2SPacket(atlasID, chunkX, chunkZ, tile).send();
    }

    @Override
    public Identifier getTile(World world, int atlasID, int chunkX, int chunkZ) {
        AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, world);
        return data.getWorldData(world.getRegistryKey()).getTile(chunkX, chunkZ);
    }

    @Override
    public void putGlobalTile(World world, Identifier tile, int chunkX, int chunkZ) {
        Log.warn("Client attempted to put global tile");
    }

    @Override
    public Identifier getGlobalTile(World world, int chunkX, int chunkZ) {
        TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
        return data.getTile(chunkX, chunkZ);
    }

    @Override
    public void deleteGlobalTile(World world, int chunkX, int chunkZ) {
        Log.warn("Client attempted to delete global tile");
    }

    @Override
    public TileRenderIterator getTiles(World world, int atlasID, Rect scope, int step) {
        TileRenderIterator iter = new TileRenderIterator(AntiqueAtlasMod.tileData
                .getData(atlasID, world)
                .getWorldData(world.getRegistryKey()));
        iter.setScope(scope);
        iter.setStep(step);
        return iter;
    }
}
