package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Puts biome tile into one atlas.
 * @author Hunternif
 * @author Haven King
 */
public class PutTileS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "tile", "put");

	public PutTileS2CPacket(int atlasID, RegistryKey<World> world, int x, int z, Identifier tile) {
		this.writeInt(atlasID);
		this.writeIdentifier(world.getValue());
		this.writeVarInt(x);
		this.writeVarInt(z);
		this.writeIdentifier(tile);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		int atlasID = buf.readVarInt();
		RegistryKey<World> world = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
		int x = buf.readVarInt();
		int z = buf.readVarInt();
		Identifier tile = buf.readIdentifier();

		client.execute(() -> {
			AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, client.player.getEntityWorld());
			data.setTile(world, x, z, tile);
		});
	}
}
