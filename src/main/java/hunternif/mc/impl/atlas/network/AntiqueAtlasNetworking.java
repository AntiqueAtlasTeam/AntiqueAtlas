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
		ClientPlayNetworking.registerGlobalReceiver(CustomTileInfoS2CPacket.ID, CustomTileInfoS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(DeleteCustomGlobalTileS2CPacket.ID, DeleteCustomGlobalTileS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(DeleteMarkerResponseS2CPacket.ID, DeleteMarkerResponseS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(DimensionUpdateS2CPacket.ID, DimensionUpdateS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(MapDataS2CPacket.ID, MapDataS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(MarkersS2CPacket.ID, MarkersS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(PutTileS2CPacket.ID, PutTileS2CPacket::apply);
		ClientPlayNetworking.registerGlobalReceiver(TileGroupsS2CPacket.ID, TileGroupsS2CPacket::apply);
	}

	public static void registerC2SListeners() {
		ServerPlayNetworking.registerGlobalReceiver(AddMarkerC2SPacket.ID, AddMarkerC2SPacket::apply);
		ServerPlayNetworking.registerGlobalReceiver(BrowsingPositionC2SPacket.ID, BrowsingPositionC2SPacket::apply);
		ServerPlayNetworking.registerGlobalReceiver(DeleteMarkerRequestC2SPacket.ID, DeleteMarkerRequestC2SPacket::apply);
		ServerPlayNetworking.registerGlobalReceiver(PutTileC2SPacket.ID, PutTileC2SPacket::apply);
	}
}
