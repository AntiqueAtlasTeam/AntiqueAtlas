package hunternif.mc.impl.atlas.network.packet.s2c.play;

import dev.architectury.networking.NetworkManager;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Sent from server to client to remove a custom global tile.
 * @author Hunternif
 * @author Haven King
 */
public class DeleteGlobalTileS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "global_tile", "delete");

	public DeleteGlobalTileS2CPacket(RegistryKey<World> world, int chunkX, int chunkZ) {
		this.writeIdentifier(world.getValue());
		this.writeVarInt(chunkX);
		this.writeVarInt(chunkZ);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static void apply(PacketByteBuf buf, NetworkManager.PacketContext context) {
		RegistryKey<World> world = RegistryKey.of(RegistryKeys.WORLD, buf.readIdentifier());
		int chunkX = buf.readVarInt();
		int chunkZ = buf.readVarInt();

		context.queue(() -> {
			TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
			data.removeTile(chunkX, chunkZ);
		});
	}
}
