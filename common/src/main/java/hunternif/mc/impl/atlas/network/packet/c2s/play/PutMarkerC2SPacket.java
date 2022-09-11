package hunternif.mc.impl.atlas.network.packet.c2s.play;

import dev.architectury.networking.NetworkManager;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * A request from a client to create a new marker. In order to prevent griefing,
 * the marker has to be local.
 * @author Hunternif
 * @author Haven King
 */
public class PutMarkerC2SPacket extends C2SPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "marker", "put");

	public PutMarkerC2SPacket(int atlasID, Identifier markerType, int x, int z, boolean visibleBeforeDiscovery, Text label) {
		this.writeVarInt(atlasID);
		this.writeIdentifier(markerType);
		this.writeVarInt(x);
		this.writeVarInt(z);
		this.writeBoolean(visibleBeforeDiscovery);
		this.writeText(label);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(PacketByteBuf buf, NetworkManager.PacketContext context) {
		int atlasID = buf.readVarInt();
		Identifier markerType = buf.readIdentifier();
		int x = buf.readVarInt();
		int z = buf.readVarInt();
		boolean visibleBeforeDiscovery = buf.readBoolean();
		Text label = buf.readText();

		context.queue(() -> {
			if (!AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
				AntiqueAtlasMod.LOG.warn(
								"Player {} attempted to put marker into someone else's Atlas #{}}",
						context.getPlayer().getName(), atlasID);
				return;
			}

			AtlasAPI.getMarkerAPI().putMarker(context.getPlayer().getEntityWorld(), visibleBeforeDiscovery, atlasID, markerType, label, x,z);
		});
	}
}
