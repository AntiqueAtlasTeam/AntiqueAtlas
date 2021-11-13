package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;

import java.util.function.Supplier;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * Puts biome tile into one atlas. When sent to server, forwards it to every
 * client that has this atlas' data synced.
 * @author Hunternif
 * @author Haven King
 */
public class PutTileC2SPacket extends C2SPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "put");

	int atlasID, x, z; 
	ResourceLocation tile;
	
	public PutTileC2SPacket(int atlasID, int x, int z, ResourceLocation tile) {
		this.atlasID = atlasID;
		this.x = x;
		this.z = z;
		this.tile = tile;
	}

	public static void encode(final PutTileC2SPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeInt(msg.atlasID);
		packetBuffer.writeVarInt(msg.x);
		packetBuffer.writeVarInt(msg.z);
		packetBuffer.writeResourceLocation(msg.tile);
	}

	public static PutTileC2SPacket decode(final FriendlyByteBuf packetBuffer) {
		return new PutTileC2SPacket(
				packetBuffer.readVarInt(),
						packetBuffer.readVarInt(),
						packetBuffer.readVarInt(),
						packetBuffer.readResourceLocation());
	}

	public static void handle(final PutTileC2SPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayer sender = context.getSender();
			if (sender == null) {
				return;
			}
			if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(sender).contains(msg.atlasID)) {
				Log.warn("Player %s attempted to modify someone else's Atlas #%d",
						sender.getName(), msg.atlasID);
				return;
			}

			AtlasAPI.getTileAPI().putTile(sender.getCommandSenderWorld(), msg.atlasID, msg.tile, msg.x, msg.z);
		});
		context.setPacketHandled(true);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
