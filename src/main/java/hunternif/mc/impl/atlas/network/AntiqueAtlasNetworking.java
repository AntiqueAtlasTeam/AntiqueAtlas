package hunternif.mc.impl.atlas.network;

import hunternif.mc.impl.atlas.network.packet.c2s.play.*;
import hunternif.mc.impl.atlas.network.packet.s2c.play.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class AntiqueAtlasNetworking {
	@Environment(EnvType.CLIENT)
	public static void registerS2CListeners() {
		ClientPlayNetworking.registerGlobalReceiver(PutGlobalTileS2CPacket.ID, PutGlobalTileS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(DeleteGlobalTileS2CPacket.ID, DeleteGlobalTileS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(DeleteMarkerS2CPacket.ID, DeleteMarkerS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(DimensionUpdateS2CPacket.ID, DimensionUpdateS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(MapDataS2CPacket.ID, MapDataS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(PutMarkersS2CPacket.ID, PutMarkersS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(PutTileS2CPacket.ID, PutTileS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(TileGroupsS2CPacket.ID, TileGroupsS2CPacket::apply);
	}

	public static void registerC2SListeners() {
		ServerPlayNetworking.registerGlobalReceiver(PutMarkerC2SPacket.ID, PutMarkerC2SPacket::apply);
		ServerPlayNetworking.registerGlobalReceiver(PutBrowsingPositionC2SPacket.ID, PutBrowsingPositionC2SPacket::apply);
		ServerPlayNetworking.registerGlobalReceiver(DeleteMarkerC2SPacket.ID, DeleteMarkerC2SPacket::apply);
		ServerPlayNetworking.registerGlobalReceiver(PutTileC2SPacket.ID, PutTileC2SPacket::apply);
	}
}
