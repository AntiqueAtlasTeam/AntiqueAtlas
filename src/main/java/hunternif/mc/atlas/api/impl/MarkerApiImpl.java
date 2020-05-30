package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.bidirectional.DeleteMarkerPacket;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.network.server.AddMarkerPacket;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

public class MarkerApiImpl implements MarkerAPI {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;

	@Nullable
	@Override
	public Marker putMarker(@Nonnull World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
		return doPutMarker(world, visibleAhead, atlasID, markerType, label, x, z);
	}
	@Nullable
	@Override
	public Marker putGlobalMarker(@Nonnull World world, boolean visibleAhead, String markerType, String label, int x, int z) {
		return doPutMarker(world, visibleAhead, GLOBAL, markerType, label, x, z);
	}
	private Marker doPutMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
		Marker marker = null;
		if (world.isRemote) {
			if (atlasID == GLOBAL) {
				Log.warn("Client tried to add a global marker!");
			} else {
				PacketDispatcher.INSTANCE.sendToServer(new AddMarkerPacket(atlasID,
						world.dimension.getType(), markerType, label, x, z, visibleAhead));
			}
		} else {
			if (atlasID == GLOBAL) {
				MarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
				marker = data.createAndSaveMarker(markerType, label, world.dimension.getType(), x, z, visibleAhead);
				PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), new MarkersPacket(world.dimension.getType(), marker));
			} else {
				MarkersData data = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
				marker = data.createAndSaveMarker(markerType, label, world.dimension.getType(), x, z, visibleAhead);
				PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), new MarkersPacket(atlasID, world.dimension.getType(), marker));
			}
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
		DeleteMarkerPacket packet = atlasID == GLOBAL ?
				new DeleteMarkerPacket(markerID) :
				new DeleteMarkerPacket(atlasID, markerID);
		if (world.isRemote) {
			if (atlasID == GLOBAL) {
				Log.warn("Client tried to delete a global marker!");
			} else {
				PacketDispatcher.INSTANCE.sendToServer(packet);
			}
		} else {
			MarkersData data = atlasID == GLOBAL ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
			data.removeMarker(markerID);
			PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
		}
	}
	
	@Override
	public void registerMarker(ResourceLocation ResourceLocation, MarkerType markerType) {
		MarkerRegistry.register(ResourceLocation, markerType);
	}

}
