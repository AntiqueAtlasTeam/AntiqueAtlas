package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

public class PlayerEventHandler {
    public static void onPlayerLogin(ServerPlayerEntity player) {
        World world = player.world;
        int atlasID = player.getUniqueID().hashCode();

        AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
        // On the player join send the map from the server to the client:
        if (!data.isEmpty()) {
            data.syncOnPlayer(atlasID, player);
        }

        // Same thing with the local markers:
        MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        if (!markers.isEmpty()) {
            markers.syncOnPlayer(atlasID, player);
        }
    }

    public static void onPlayerTick(PlayerEntity player) {
        AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(
                player.getUniqueID().hashCode(), player.world);

        // Updating map around player
        data.updateMapAroundPlayer(player);
    }
}