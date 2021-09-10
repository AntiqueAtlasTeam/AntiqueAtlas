package hunternif.mc.impl.atlas.network;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.c2s.play.AddMarkerC2SPacket;
import hunternif.mc.impl.atlas.network.packet.c2s.play.BrowsingPositionC2SPacket;
import hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket;
import hunternif.mc.impl.atlas.network.packet.c2s.play.PutTileC2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.*;
import me.shedaniel.architectury.networking.simple.MessageType;
import me.shedaniel.architectury.networking.simple.SimpleNetworkManager;

public class AntiqueAtlasNetworking {
    private static SimpleNetworkManager NET = SimpleNetworkManager.create(AntiqueAtlasMod.ID);

    public static MessageType CUSTOM_TILE_INFO = NET.registerS2C(CustomTileInfoS2CPacket.ID.getPath(), CustomTileInfoS2CPacket::new);
    public static MessageType DELETE_CUSTOM_GLOBAL_TILE = NET.registerS2C(DeleteCustomGlobalTileS2CPacket.ID.getPath(), DeleteCustomGlobalTileS2CPacket::new);
    public static MessageType DELETE_MARKER_RESPONSE = NET.registerS2C(DeleteMarkerResponseS2CPacket.ID.getPath(), DeleteMarkerResponseS2CPacket::new);
    public static MessageType DIMENSION_UPDATE = NET.registerS2C(DimensionUpdateS2CPacket.ID.getPath(), DimensionUpdateS2CPacket::new);
    public static MessageType MAP_DATA = NET.registerS2C(MapDataS2CPacket.ID.getPath(), MapDataS2CPacket::new);
    public static MessageType MARKERS = NET.registerS2C(MarkersS2CPacket.ID.getPath(), MarkersS2CPacket::new);
    public static MessageType PUT_TILE = NET.registerS2C(PutTileS2CPacket.ID.getPath(), PutTileS2CPacket::new);
    public static MessageType TILE_GROUPS = NET.registerS2C(TileGroupsS2CPacket.ID.getPath(), TileGroupsS2CPacket::new);

    public static MessageType ADD_MARKER = NET.registerC2S(AddMarkerC2SPacket.ID.getPath(), AddMarkerC2SPacket::new);
    public static MessageType BROWSING_POSITION = NET.registerC2S(BrowsingPositionC2SPacket.ID.getPath(), BrowsingPositionC2SPacket::new);
    public static MessageType DELETE_MARKER_REQUEST = NET.registerC2S(DeleteMarkerRequestC2SPacket.ID.getPath(), DeleteMarkerRequestC2SPacket::new);
    public static MessageType PUT_TILE_C2S = NET.registerC2S(PutTileC2SPacket.ID.getPath(), PutTileC2SPacket::new);

    public static void initialize() {
    }
}
