package hunternif.mc.impl.atlas.api.impl;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.MarkerAPI;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.DeleteMarkerResponseS2CPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.MarkersS2CPacket;
import hunternif.mc.impl.atlas.util.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Collections;

public class MarkerApiImpl implements MarkerAPI {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;

	@Nullable
	@Override
	public Marker putMarker(@Nonnull World world, boolean visibleAhead, int atlasID, ResourceLocation marker, ITextComponent label, int x, int z) {
		return doPutMarker(world, visibleAhead, atlasID, marker, label, x, z);
	}
	@Nullable
	@Override
	public Marker putGlobalMarker(@Nonnull World world, boolean visibleAhead, ResourceLocation marker, ITextComponent label, int x, int z) {
		return doPutMarker(world, visibleAhead, GLOBAL, marker, label, x, z);
	}

	private Marker doPutMarker(World world, boolean visibleAhead, int atlasID, ResourceLocation markerId, ITextComponent label, int x, int z) {
		Marker marker = null;
		if (!world.isRemote && world.getServer() != null) {
			MarkersData data = atlasID == GLOBAL
							? AntiqueAtlasMod.globalMarkersData.getData()
							: AntiqueAtlasMod.markersData.getMarkersData(atlasID, world)
							;

			marker = data.createAndSaveMarker(markerId, world.getDimensionKey(), x, z, visibleAhead, label);
			new MarkersS2CPacket(atlasID, world.getDimensionKey(), Collections.singleton(marker)).send((ServerWorld) world);
		}

		return marker;
	}
	
	@Override
	public void deleteMarker(@Nonnull World world, int atlasID, int markerID) {
		doDeleteMarker(world, atlasID, markerID);
	}

	@Override
	public void deleteGlobalMarker(@Nonnull World world, int markerID) {
		doDeleteMarker(world, GLOBAL, markerID);
	}

	private void doDeleteMarker(World world, int atlasID, int markerID) {
		if (world.isRemote) {
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
