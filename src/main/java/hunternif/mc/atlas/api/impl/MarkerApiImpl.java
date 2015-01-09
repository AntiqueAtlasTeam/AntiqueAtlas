package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.bidirectional.GlobalMarkersPacket;
import hunternif.mc.atlas.network.bidirectional.MarkersPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MarkerApiImpl implements MarkerAPI {

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
		Marker marker = new Marker(markerType, label, x, z, visibleAhead);
		MarkersPacket packet = new MarkersPacket(atlasID, world.provider.dimensionId, marker);
		if (!world.isRemote) {
			MarkersData data = AntiqueAtlasMod.itemAtlas.getMarkersData(atlasID, world);
			if (data == null) {
				AntiqueAtlasMod.logger.warn("Tried to put marker into non-existent Atlas ID: " + atlasID);
				return;
			}
			data.putMarker(world.provider.dimensionId, marker);
			data.markDirty();
			PacketDispatcher.sendToAll(packet);
		} else {
			PacketDispatcher.sendToServer(packet);
		}
	}

	@Override
	public void putGlobalMarker(World world, boolean visibleAhead, String markerType, String label, int x, int z) {
		Marker marker = new Marker(markerType, label, x, z, visibleAhead);
		GlobalMarkersPacket packet = new GlobalMarkersPacket(world.provider.dimensionId, marker);
		if (!world.isRemote) {
			MarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
			data.putMarker(world.provider.dimensionId, marker);
			data.markDirty();
			PacketDispatcher.sendToAll(packet);
		} else {
			PacketDispatcher.sendToServer(packet);
		}
	}

}
