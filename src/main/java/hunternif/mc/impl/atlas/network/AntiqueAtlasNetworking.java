package hunternif.mc.impl.atlas.network;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.c2s.play.*;
import hunternif.mc.impl.atlas.network.packet.s2c.play.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class AntiqueAtlasNetworking {
	@OnlyIn(Dist.CLIENT)
	public static void registerS2CListeners() {
		SimpleChannel channel = AntiqueAtlasMod.MOD_CHANNEL;
		channel.registerMessage(0, CustomTileInfoS2CPacket.class, CustomTileInfoS2CPacket::encode, CustomTileInfoS2CPacket::decode, CustomTileInfoS2CPacket::message);
		channel.registerMessage(1, DeleteCustomGlobalTileS2CPacket.class, DeleteCustomGlobalTileS2CPacket::encode, DeleteCustomGlobalTileS2CPacket::decode, DeleteCustomGlobalTileS2CPacket::message);
		channel.registerMessage(2, DeleteMarkerResponseS2CPacket.class, DeleteMarkerResponseS2CPacket::encode, DeleteMarkerResponseS2CPacket::decode, DeleteMarkerResponseS2CPacket::message);
		channel.registerMessage(3, DimensionUpdateS2CPacket.class, DimensionUpdateS2CPacket::encode, DimensionUpdateS2CPacket::decode, DimensionUpdateS2CPacket::message);
		channel.registerMessage(4, MapDataS2CPacket.class, MapDataS2CPacket::encode, MapDataS2CPacket::decode, MapDataS2CPacket::message);
		channel.registerMessage(5, MarkersS2CPacket.class, MarkersS2CPacket::encode, MarkersS2CPacket::decode, MarkersS2CPacket::message);
		channel.registerMessage(6, PutTileS2CPacket.class, PutTileS2CPacket::encode, PutTileS2CPacket::decode, PutTileS2CPacket::message);
		channel.registerMessage(7, TileGroupsS2CPacket.class, TileGroupsS2CPacket::encode, TileGroupsS2CPacket::decode, TileGroupsS2CPacket::message);
	}

	public static void registerC2SListeners() {
		SimpleChannel channel = AntiqueAtlasMod.MOD_CHANNEL;
		channel.registerMessage(8, AddMarkerC2SPacket.class, AddMarkerC2SPacket::encode, AddMarkerC2SPacket::decode, AddMarkerC2SPacket::handle);
		channel.registerMessage(9, BrowsingPositionC2SPacket.class, BrowsingPositionC2SPacket::encode, BrowsingPositionC2SPacket::decode, BrowsingPositionC2SPacket::handle);
		channel.registerMessage(10, DeleteMarkerRequestC2SPacket.class, DeleteMarkerRequestC2SPacket::encode, DeleteMarkerRequestC2SPacket::decode, DeleteMarkerRequestC2SPacket::handle);
		channel.registerMessage(10, PutTileC2SPacket.class, PutTileC2SPacket::encode, PutTileC2SPacket::decode, PutTileC2SPacket::handle);
	}
}
