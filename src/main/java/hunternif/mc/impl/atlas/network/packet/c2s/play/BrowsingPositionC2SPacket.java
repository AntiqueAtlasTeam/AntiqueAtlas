package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

/**
 * Packet used to save the last browsing position for a dimension in an atlas.
 * @author Hunternif
 * @author Haven King
 */
public class BrowsingPositionC2SPacket extends C2SPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "browsing_position");

	public BrowsingPositionC2SPacket(int atlasID, RegistryKey<World> world, int x, int y, double zoom) {
		this.writeVarInt(atlasID);
		this.writeIdentifier(world.getValue());
		this.writeVarInt(x);
		this.writeVarInt(y);
		this.writeDouble(zoom);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(PacketContext context, PacketByteBuf buf) {
		int atlasID = buf.readVarInt();
		RegistryKey<World> world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
		int x = buf.readVarInt();
		int y = buf.readVarInt();
		double zoom = buf.readDouble();

		context.getTaskQueue().execute(() -> {
			if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
				Log.warn("Player %s attempted to put position marker into someone else's Atlas #%d",
						context.getPlayer().getCommandSource().getName(), atlasID);
				return;
			}

			AntiqueAtlasMod.atlasData.getAtlasData(atlasID, context.getPlayer().getEntityWorld())
					.getWorldData(world).setBrowsingPosition(x, y, zoom);
		});
	}
}
