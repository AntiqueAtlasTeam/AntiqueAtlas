package hunternif.mc.impl.atlas.api.client.impl;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.MarkerAPI;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.network.packet.c2s.play.AddMarkerC2SPacket;
import hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MarkerApiImplClient implements MarkerAPI {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;

	@Override
	public void registerMarker(ResourceLocation identifier, MarkerType markerType) {
		MarkerType.register(identifier, markerType);
	}

	@Nullable
	@Override
	public Marker putMarker(@Nonnull World world, boolean visibleAhead, int atlasID, MarkerType markerType, ITextComponent label, int x, int z) {
		new AddMarkerC2SPacket(atlasID, MarkerType.REGISTRY.getKey(markerType), x, z, visibleAhead, label).send();
		return null;
	}

	@Nullable
	@Override
	public Marker putGlobalMarker(@Nonnull World world, boolean visibleAhead, MarkerType markerType, ITextComponent label, int x, int z) {
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
