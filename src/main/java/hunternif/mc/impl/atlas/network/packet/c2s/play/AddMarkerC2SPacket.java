package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.MarkersS2CPacket;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;

/**
 * A request from a client to create a new marker. In order to prevent griefing,
 * the marker has to be local.
 * @author Hunternif
 * @author Haven King
 */
public class AddMarkerC2SPacket extends C2SPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "marker", "add");

	public AddMarkerC2SPacket(int atlasID, MarkerType markerType, int x, int z, boolean visibleBeforeDiscovery, Text label) {
		this.writeVarInt(atlasID);
		this.writeIdentifier(MarkerType.REGISTRY.getId(markerType));
		this.writeVarInt(x);
		this.writeVarInt(z);
		this.writeBoolean(visibleBeforeDiscovery);
		this.writeText(label);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(PacketContext context, PacketByteBuf buf) {
		int atlasID = buf.readVarInt();
		Identifier markerType = buf.readIdentifier();
		int x = buf.readVarInt();
		int z = buf.readVarInt();
		boolean visibleBeforeDiscovery = buf.readBoolean();
		Text label = buf.readText();

		context.getTaskQueue().execute(() -> {
			ServerPlayerEntity playerEntity = (ServerPlayerEntity) context.getPlayer();
			if (!AtlasAPI.getPlayerAtlases(playerEntity).contains(atlasID)) {
				AntiqueAtlasMod.LOG.warn(
								"Player {} attempted to put marker into someone else's Atlas #{}}",
								playerEntity.getName(), atlasID);
				return;
			}

			if (playerEntity.getServer() != null) {
				MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, playerEntity.getEntityWorld());
				Marker marker = markersData.createAndSaveMarker(
								MarkerType.REGISTRY.get(markerType),
								context.getPlayer().getEntityWorld().getRegistryKey(),
								x,
								z,
								visibleBeforeDiscovery,
								label);

				new MarkersS2CPacket(atlasID, context.getPlayer().getEntityWorld().getRegistryKey(), Collections.singleton(marker)).send(playerEntity.server);
			}
		});
	}
}
