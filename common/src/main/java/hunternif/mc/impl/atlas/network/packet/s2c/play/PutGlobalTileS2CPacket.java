package hunternif.mc.impl.atlas.network.packet.s2c.play;

import dev.architectury.networking.NetworkManager;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 * @author Haven King
 */
public class PutGlobalTileS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "global_tile", "put");

	public PutGlobalTileS2CPacket(RegistryKey<World> world, List<Map.Entry<ChunkPos, Identifier>> tiles) {
		this.writeIdentifier(world.getValue());
		this.writeVarInt(tiles.size());

		for (Map.Entry<ChunkPos, Identifier> entry : tiles) {
			this.writeVarInt(entry.getKey().x);
			this.writeVarInt(entry.getKey().z);
			this.writeIdentifier(entry.getValue());
		}
	}

	public PutGlobalTileS2CPacket(RegistryKey<World> world, int chunkX, int chunkZ, Identifier tileId) {
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

	@Environment(EnvType.CLIENT)
	public static void apply(PacketByteBuf buf, NetworkManager.PacketContext context) {
		RegistryKey<World> world = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
		int tileCount = buf.readVarInt();

		TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
		for (int i = 0; i < tileCount; ++i) {
			data.setTile(buf.readVarInt(), buf.readVarInt(), buf.readIdentifier());
		}
	}
}
