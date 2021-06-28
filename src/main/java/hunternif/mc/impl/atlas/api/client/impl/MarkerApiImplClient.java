package hunternif.mc.impl.atlas.api.client.impl;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.MarkerAPI;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.network.packet.c2s.play.AddMarkerC2SPacket;
import hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Environment(EnvType.CLIENT)
public class MarkerApiImplClient implements MarkerAPI {
    @Nullable
    @Override
    public Marker putMarker(@NotNull World world, boolean visibleAhead, int atlasID, Identifier marker, Text label, int x, int z) {
        new AddMarkerC2SPacket(atlasID, marker, x, z, visibleAhead, label).send();
        return null;
    }

    @Nullable
    @Override
    public Marker putGlobalMarker(@NotNull World world, boolean visibleAhead, Identifier marker, Text label, int x, int z) {
        AntiqueAtlasMod.LOG.warn("Client tried to add a global marker");

        return null;
    }

    @Override
    public void deleteMarker(@NotNull World world, int atlasID, int markerID) {
        new DeleteMarkerRequestC2SPacket(atlasID, markerID).send();
    }

    @Override
    public void deleteGlobalMarker(@NotNull World world, int markerID) {
        AntiqueAtlasMod.LOG.warn("Client tried to delete a global marker");
    }
}
