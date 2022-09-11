package hunternif.mc.impl.atlas.network;

import dev.architectury.networking.NetworkManager;
import hunternif.mc.impl.atlas.network.packet.c2s.play.*;
import hunternif.mc.impl.atlas.network.packet.s2c.play.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class AntiqueAtlasNetworking {
	@Environment(EnvType.CLIENT)
	public static void registerS2CListeners() {
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, PutGlobalTileS2CPacket.ID, PutGlobalTileS2CPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, DeleteGlobalTileS2CPacket.ID, DeleteGlobalTileS2CPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, DeleteMarkerS2CPacket.ID, DeleteMarkerS2CPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, DimensionUpdateS2CPacket.ID, DimensionUpdateS2CPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, MapDataS2CPacket.ID, MapDataS2CPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, PutMarkersS2CPacket.ID, PutMarkersS2CPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, PutTileS2CPacket.ID, PutTileS2CPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, TileGroupsS2CPacket.ID, TileGroupsS2CPacket::apply);
	}

	public static void registerC2SListeners() {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, PutMarkerC2SPacket.ID, PutMarkerC2SPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, PutBrowsingPositionC2SPacket.ID, PutBrowsingPositionC2SPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, DeleteMarkerC2SPacket.ID, DeleteMarkerC2SPacket::apply);
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, PutTileC2SPacket.ID, PutTileC2SPacket::apply);
	}
}
