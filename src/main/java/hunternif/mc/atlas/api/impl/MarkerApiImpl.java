package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.GlobalMarkersPacket;
import hunternif.mc.atlas.network.MarkersPacket;
import hunternif.mc.atlas.util.NetworkUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;

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
	public void putMarker(World world, int dimension, int atlasID, String markerType, String label, int x, int z) {
		Marker marker = new Marker(markerType, label, x, z);
		MarkersPacket packet = new MarkersPacket(atlasID, dimension, marker);
		if (!world.isRemote) {
			MarkersData data = AntiqueAtlasMod.itemAtlas.getMarkersData(atlasID, world);
			if (data == null) {
				AntiqueAtlasMod.logger.warning("Tried to put marker into non-existent Atlas ID: " + atlasID);
				return;
			}
			data.putMarker(dimension, marker);
			data.markDirty();
			NetworkUtil.sendPacketToAllPlayersInWorld(world, packet.makePacket());
		} else {
			PacketDispatcher.sendPacketToServer(packet.makePacket());
		}
	}

	@Override
	public void putGlobalMarker(World world, int dimension, String markerType, String label, int x, int z) {
		GlobalMarkersPacket packet = new GlobalMarkersPacket(dimension, new Marker(markerType, label, x, z));
		if (!world.isRemote) {
			NetworkUtil.sendPacketToAllPlayersInWorld(world, packet.makePacket());
		} else {
			PacketDispatcher.sendPacketToServer(packet.makePacket());
		}
	}

}
