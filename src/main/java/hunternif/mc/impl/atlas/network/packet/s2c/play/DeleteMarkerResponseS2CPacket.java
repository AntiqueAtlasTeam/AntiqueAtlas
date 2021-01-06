package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Deletes a marker. A client sends a {@link hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket}
 * to the server as a request, and the server sends this back to all players as a response, including the
 * original sender.
 * @author Hunternif
 * @author Haven King
 */
public class DeleteMarkerResponseS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "s2c", "marker", "delete");

	private static final int GLOBAL = -1;

	int atlasID, markerID;

	public DeleteMarkerResponseS2CPacket(int atlasID, int markerID) {
		this.atlasID = atlasID;
		this.markerID = markerID;
	}

	public static void encode(final DeleteMarkerResponseS2CPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeVarInt(msg.markerID);
	}

	public static DeleteMarkerResponseS2CPacket decode(final PacketBuffer packetBuffer) {
		int atlasID = packetBuffer.readVarInt();
		int markerID = packetBuffer.readVarInt();

		return new DeleteMarkerResponseS2CPacket(atlasID, markerID);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(ClientPlayerEntity player) {
		MarkersData data = this.atlasID == GLOBAL ?
				AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.markersData.getMarkersData(this.atlasID, Minecraft.getInstance().player.getEntityWorld());
				data.removeMarker(this.markerID);
				return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	//	public static void apply(PacketContext context, PacketByteBuf buf) {
	//		int atlasID = buf.readVarInt();
	//		int markerID = buf.readVarInt();
	//
	//		context.getTaskQueue().execute(() -> {
	//			MarkersData data = atlasID == GLOBAL ?
	//					AntiqueAtlasMod.globalMarkersData.getData() :
	//					AntiqueAtlasMod.markersData.getMarkersData(atlasID, context.getPlayer().getEntityWorld());
	//			data.removeMarker(markerID);
	//		});
	//	}
}
