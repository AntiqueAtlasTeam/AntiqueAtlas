package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.core.TileKind;
import hunternif.mc.impl.atlas.core.TileKindFactory;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Puts biome tile into one atlas. When sent to server, forwards it to every
 * client that has this atlas' data synced.
 * @author Hunternif
 * @author Haven King
 */
public class PutTileC2SPacket extends C2SPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "put");

	public PutTileC2SPacket(int atlasID, int x, int z, TileKind tileKind) {
		this.writeInt(atlasID);
		this.writeVarInt(x);
		this.writeVarInt(z);
		this.writeVarInt(tileKind.getId());
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(PacketContext context, PacketByteBuf buf) {
		int atlasID = buf.readVarInt();
		int x = buf.readVarInt();
		int z = buf.readVarInt();
		TileKind kind = TileKindFactory.get(buf.readVarInt());

		context.getTaskQueue().execute(() -> {
			if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
				Log.warn("Player %s attempted to modify someone else's Atlas #%d",
						context.getPlayer().getName(), atlasID);
				return;
			}
			if (kind.getId() >= 0) {
				AtlasAPI.tiles.putBiomeTile(context.getPlayer().getEntityWorld(), atlasID, kind.getBiome(), x, z);
			} else {
				AtlasAPI.tiles.putCustomTile(context.getPlayer().getEntityWorld(), atlasID, kind.getExtTile(), x, z);
			}
		});
	}
}
