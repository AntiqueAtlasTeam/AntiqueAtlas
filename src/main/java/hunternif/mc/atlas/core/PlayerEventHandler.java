package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerEventHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.player;
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

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(
                event.player.getUniqueID().hashCode(), event.player.world);

        // Updating map around player
        data.updateMapAroundPlayer(event.player);
    }
}