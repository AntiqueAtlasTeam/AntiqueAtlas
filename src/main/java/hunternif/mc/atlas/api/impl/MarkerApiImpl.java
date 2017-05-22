package hunternif.mc.atlas.api.impl;

import net.minecraft.world.World;

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

public class MarkerApiImpl implements MarkerAPI {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;
	
	@Override
	public void putMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
		doPutMarker(world, visibleAhead, atlasID, markerType, label, x, z);
	}
	@Override
	public void putGlobalMarker(World world, boolean visibleAhead, String markerType, String label, int x, int z) {
		doPutMarker(world, visibleAhead, GLOBAL, markerType, label, x, z);
	}
	private void doPutMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
		if (world.isRemote) {
			if (atlasID == GLOBAL) {
				Log.warn("Client tried to add a global marker!");
			} else {
				PacketDispatcher.sendToServer(new AddMarkerPacket(atlasID,
						world.provider.getDimension(), markerType, label, x, z, visibleAhead));
			}
		} else {
			if (atlasID == GLOBAL) {
				MarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
				Marker marker = data.createAndSaveMarker(markerType, label, world.provider.getDimension(), x, z, visibleAhead);
				PacketDispatcher.sendToAll(new MarkersPacket(world.provider.getDimension(), marker));
			} else {
				MarkersData data = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
				Marker marker = data.createAndSaveMarker(markerType, label, world.provider.getDimension(), x, z, visibleAhead);
				PacketDispatcher.sendToAll(new MarkersPacket(atlasID, world.provider.getDimension(), marker));
			}
		}
	}
	
	@Override
	public void deleteMarker(World world, int atlasID, int markerID) {
		doDeleteMarker(world, atlasID, markerID);
	}
	@Override
	public void deleteGlobalMarker(World world, int markerID) {
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
				PacketDispatcher.sendToServer(packet);
			}
		} else {
			MarkersData data = atlasID == GLOBAL ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
			data.removeMarker(markerID);
			PacketDispatcher.sendToAll(packet);
		}
	}
	
	@Override
	public void registerMarker(MarkerType markerType) {
		MarkerRegistry.register(markerType);
	}

}
