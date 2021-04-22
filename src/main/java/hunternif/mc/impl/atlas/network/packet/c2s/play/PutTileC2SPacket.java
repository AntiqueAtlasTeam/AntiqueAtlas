package hunternif.mc.impl.atlas.network.packet.c2s.play;

import java.util.function.Supplier;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

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

	public static void encode(final PutTileC2SPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.atlasID);
		packetBuffer.writeVarInt(msg.x);
		packetBuffer.writeVarInt(msg.z);
		packetBuffer.writeResourceLocation(msg.tile);
	}

	public static PutTileC2SPacket decode(final PacketBuffer packetBuffer) {
		return new PutTileC2SPacket(
				packetBuffer.readVarInt(),
						packetBuffer.readVarInt(),
						packetBuffer.readVarInt(),
						packetBuffer.readResourceLocation());
	}

	public static void handle(final PutTileC2SPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayerEntity sender = context.getSender();
			if (sender == null) {
				return;
			}
			if (AntiqueAtlasConfig.itemNeeded.get() && !AtlasAPI.getPlayerAtlases(context.getSender()).contains(msg.atlasID)) {
				Log.warn("Player %s attempted to modify someone else's Atlas #%d",
						context.getSender().getName(), msg.atlasID);
				return;
			}
			
			AtlasAPI.getTileAPI().putTile(sender.getEntityWorld(), msg.atlasID, msg.tile, msg.x, msg.z);
		});
		context.setPacketHandled(true);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
