package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.bidirectional.DeleteMarkerPacket;
import hunternif.mc.atlas.network.server.AddMarkerPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MarkerApiImpl implements MarkerAPI {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;

	@Override
	public void setTexture(String markerType, ResourceLocation texture) {
		MarkerTextureMap.instance().setTexture(markerType, texture);
	}

	@Override
	public boolean setTextureIfNone(String markerType, ResourceLocation texture) {
		return MarkerTextureMap.instance().setTextureIfNone(markerType, texture);
	}

	@Override
	public void save() {
		AntiqueAtlasMod.proxy.updateMarkerTextureConfig();
	}
	
	@Override
	public void putMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
		doPutMarker(world, visibleAhead, atlasID, markerType, label, x, z);
	}
	@Override
	public void putGlobalMarker(World world, boolean visibleAhead, String markerType, String label, int x, int z) {
		doPutMarker(world, visibleAhead, GLOBAL, markerType, label, x, z);
	}
	private void doPutMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
		AddMarkerPacket packet = atlasID == GLOBAL ?
				new AddMarkerPacket(world.provider.dimensionId, markerType, label, x, z, visibleAhead) :
				new AddMarkerPacket(atlasID, world.provider.dimensionId, markerType, label, x, z, visibleAhead);
		if (world.isRemote) {
			PacketDispatcher.sendToServer(packet);
		} else {
			MarkersData data = atlasID == GLOBAL ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.itemAtlas.getMarkersData(atlasID, world);
			data.addMarker(markerType, label, world.provider.dimensionId, x, z, visibleAhead);
			PacketDispatcher.sendToAll(packet);
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
		if (world.isRemote) {
			DeleteMarkerPacket packet = atlasID == GLOBAL ?
					new DeleteMarkerPacket(markerID) :
					new DeleteMarkerPacket(atlasID, markerID);
			PacketDispatcher.sendToServer(packet);
		} else {
			MarkersData data = atlasID == GLOBAL ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.itemAtlas.getMarkersData(atlasID, world);
			data.removeMarker(markerID);
			DeleteMarkerPacket packet = atlasID == GLOBAL ?
					new DeleteMarkerPacket(markerID) :
					new DeleteMarkerPacket(atlasID, markerID);
			PacketDispatcher.sendToAll(packet);
		}
	}

}
