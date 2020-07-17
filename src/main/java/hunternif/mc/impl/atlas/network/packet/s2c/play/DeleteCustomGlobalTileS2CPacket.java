package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.ext.ExtBiomeData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Sent from server to client to remove a custom global tile.
 * @author Hunternif
 * @author Haven King
 */
public class DeleteCustomGlobalTileS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "delete");

	public DeleteCustomGlobalTileS2CPacket(RegistryKey<World> world, int chunkX, int chunkZ) {
		this.writeIdentifier(world.getValue());
		this.writeVarInt(chunkX);
		this.writeVarInt(chunkZ);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(PacketContext context, PacketByteBuf buf) {
		RegistryKey<World> world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
		int chunkX = buf.readVarInt();
		int chunkZ = buf.readVarInt();

		context.getTaskQueue().execute(() -> {
			ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
			data.removeBiomeAt(world, chunkX, chunkZ);
		});
	}
}
