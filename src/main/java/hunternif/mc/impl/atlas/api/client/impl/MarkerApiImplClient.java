package hunternif.mc.impl.atlas.api.client.impl;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.MarkerAPI;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.network.packet.c2s.play.AddMarkerC2SPacket;
import hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MarkerApiImplClient implements MarkerAPI {
	@Nullable
	@Override
	public Marker putMarker(@Nonnull World world, boolean visibleAhead, int atlasID, ResourceLocation marker, ITextComponent label, int x, int z) {
		new AddMarkerC2SPacket(atlasID, marker, x, z, visibleAhead, label).send();
		return null;
	}

	@Nullable
	@Override
	public Marker putGlobalMarker(@Nonnull World world, boolean visibleAhead, ResourceLocation marker, ITextComponent label, int x, int z) {
		AntiqueAtlasMod.LOG.warn("Client tried to add a global marker");

		return null;
	}

	@Override
	public void deleteMarker(@Nonnull World world, int atlasID, int markerID) {
		new DeleteMarkerRequestC2SPacket(atlasID, markerID).send();
	}

	@Override
	public void deleteGlobalMarker(@Nonnull World world, int markerID) {
		AntiqueAtlasMod.LOG.warn("Client tried to delete a global marker");
	}
}
