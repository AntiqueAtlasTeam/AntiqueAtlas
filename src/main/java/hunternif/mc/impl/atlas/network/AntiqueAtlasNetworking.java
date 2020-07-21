package hunternif.mc.impl.atlas.network;

import hunternif.mc.impl.atlas.network.packet.c2s.play.*;
import hunternif.mc.impl.atlas.network.packet.s2c.play.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class AntiqueAtlasNetworking {
	@Environment(EnvType.CLIENT)
	public static void registerS2CListeners() {
		ClientSidePacketRegistry.INSTANCE.register(CustomTileInfoS2CPacket.ID, CustomTileInfoS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(DeleteCustomGlobalTileS2CPacket.ID, DeleteCustomGlobalTileS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(DeleteMarkerResponseS2CPacket.ID, DeleteMarkerResponseS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(DimensionUpdateS2CPacket.ID, DimensionUpdateS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(MapDataS2CPacket.ID, MapDataS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(MarkersS2CPacket.ID, MarkersS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(OpenAtlasS2CPacket.ID, OpenAtlasS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(PutTileS2CPacket.ID, PutTileS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(TileGroupsS2CPacket.ID, TileGroupsS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(TileNameS2CPacket.ID, TileNameS2CPacket::apply);
		ClientSidePacketRegistry.INSTANCE.register(AtlasCreateS2CPacket.ID, AtlasCreateS2CPacket::apply);
	}

	public static void registerC2SListeners() {
		ServerSidePacketRegistry.INSTANCE.register(AddMarkerC2SPacket.ID, AddMarkerC2SPacket::apply);
		ServerSidePacketRegistry.INSTANCE.register(BrowsingPositionC2SPacket.ID, BrowsingPositionC2SPacket::apply);
		ServerSidePacketRegistry.INSTANCE.register(DeleteMarkerRequestC2SPacket.ID, DeleteMarkerRequestC2SPacket::apply);
		ServerSidePacketRegistry.INSTANCE.register(PutTileC2SPacket.ID, PutTileC2SPacket::apply);
		ServerSidePacketRegistry.INSTANCE.register(RegisterTileC2SPacket.ID, RegisterTileC2SPacket::apply);
	}
}
