package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Map;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 * @author Haven King
 */
public class CustomTileInfoS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "custom_tile", "info");

	public CustomTileInfoS2CPacket(RegistryKey<World> world, Map<ChunkPos, Identifier> tiles) {
		this.writeIdentifier(world.getValue());
		this.writeVarInt(tiles.size());

		for (Map.Entry<ChunkPos, Identifier> entry : tiles.entrySet()) {
			this.writeVarInt(entry.getKey().x);
			this.writeVarInt(entry.getKey().z);
			this.writeIdentifier(entry.getValue());
		}
	}

	public CustomTileInfoS2CPacket(RegistryKey<World> world, int chunkX, int chunkZ, Identifier tileId) {
		this.writeIdentifier(world.getValue());
		this.writeVarInt(1);
		this.writeVarInt(chunkX);
		this.writeVarInt(chunkZ);
		this.writeIdentifier(tileId);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		RegistryKey<World> world = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
		int tileCount = buf.readVarInt();

		TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
		for (int i = 0; i < tileCount; ++i) {
			data.setTile(buf.readVarInt(), buf.readVarInt(), buf.readIdentifier());
		}
	}
}
