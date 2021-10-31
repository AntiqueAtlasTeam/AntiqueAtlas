package hunternif.mc.impl.atlas.api.impl;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.MarkerAPI;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.DeleteMarkerResponseS2CPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.MarkersS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class MarkerApiImpl implements MarkerAPI {
    /**
     * Used in place of atlasID to signify that the marker is global.
     */
    private static final int GLOBAL = -1;

    @Nullable
    @Override
    public Marker putMarker(@NotNull Level world, boolean visibleAhead, int atlasID, ResourceLocation marker, Component label, int x, int z) {
        return doPutMarker(world, visibleAhead, atlasID, marker, label, x, z);
    }

    @Nullable
    @Override
    public Marker putGlobalMarker(@NotNull Level world, boolean visibleAhead, ResourceLocation marker, Component label, int x, int z) {
        return doPutMarker(world, visibleAhead, GLOBAL, marker, label, x, z);
    }

    private Marker doPutMarker(Level world, boolean visibleAhead, int atlasID, ResourceLocation markerId, Component label, int x, int z) {
        Marker marker = null;
        if (!world.isClientSide && world.getServer() != null) {
            MarkersData data = atlasID == GLOBAL
                    ? AntiqueAtlasMod.globalMarkersData.getData()
                    : AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);

            marker = data.createAndSaveMarker(markerId, world.dimension(), x, z, visibleAhead, label);
            new MarkersS2CPacket(atlasID, world.dimension(), Collections.singleton(marker)).send((ServerLevel) world);
        }

        return marker;
    }

    @Override
    public void deleteMarker(@NotNull Level world, int atlasID, int markerID) {
        doDeleteMarker(world, atlasID, markerID);
    }

    @Override
    public void deleteGlobalMarker(@NotNull Level world, int markerID) {
        doDeleteMarker(world, GLOBAL, markerID);
    }

    private void doDeleteMarker(Level world, int atlasID, int markerID) {
        if (world.isClientSide) {
            if (atlasID == GLOBAL) {
                Log.warn("Client tried to delete a global marker!");
            } else {
                new DeleteMarkerRequestC2SPacket(atlasID, markerID).send();
            }
        } else {
            MarkersData data = atlasID == GLOBAL ?
                    AntiqueAtlasMod.globalMarkersData.getData() :
                    AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
            data.removeMarker(markerID);

            new DeleteMarkerResponseS2CPacket(atlasID, markerID).send(world.getServer());
        }
    }
}
