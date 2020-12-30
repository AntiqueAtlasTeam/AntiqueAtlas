package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;

/**
 * Puts biome tile into one atlas. When sent to server, forwards it to every
 * client that has this atlas' data synced.
 * @author Hunternif
 * @author Haven King
 */
public class PutTileC2SPacket extends C2SPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "put");

	public PutTileC2SPacket(int atlasID, int x, int z, Identifier tile) {
		this.writeInt(atlasID);
		this.writeVarInt(x);
		this.writeVarInt(z);
		this.writeIdentifier(tile);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(PacketContext context, PacketByteBuf buf) {
		int atlasID = buf.readVarInt();
		int x = buf.readVarInt();
		int z = buf.readVarInt();
		Identifier tile = buf.readIdentifier();

		context.getTaskQueue().execute(() -> {
			if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
				Log.warn("Player %s attempted to modify someone else's Atlas #%d",
						context.getPlayer().getName(), atlasID);
				return;
			}
			if (BuiltinRegistries.BIOME.containsId(tile)) {
				AtlasAPI.tiles.putBiomeTile(context.getPlayer().getEntityWorld(), atlasID, tile, x, z);
			} else {
				AtlasAPI.tiles.putCustomTile(context.getPlayer().getEntityWorld(), atlasID, tile, x, z);
			}
		});
	}
}
