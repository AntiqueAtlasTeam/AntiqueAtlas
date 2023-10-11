package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.marker.MarkersData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class PlayerEventHandler {
    public static void onPlayerLogin(ServerPlayerEntity player) {
        World world = player.getWorld();
        int atlasID = player.getUuid().hashCode();

        AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, world);
        // On the player join send the map from the server to the client:
        if (!data.isEmpty()) {
            data.syncToPlayer(atlasID, player);
        }

        // Same thing with the local markers:
        MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        if (!markers.isEmpty()) {
            markers.syncToPlayer(atlasID, player);
        }
    }

    public static void onPlayerTick(PlayerEntity player) {
        if (!AntiqueAtlasMod.CONFIG.itemNeeded) {
            // TODO Can we move world scanning to the server in this case as well?
            AtlasData data = AntiqueAtlasMod.tileData.getData(
                    player.getUuid().hashCode(), player.getWorld());

            AntiqueAtlasMod.worldScanner.updateAtlasAroundPlayer(data, player);
        }
    }
}
