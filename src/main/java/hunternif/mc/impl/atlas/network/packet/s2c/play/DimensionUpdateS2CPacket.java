package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileInfo;
import hunternif.mc.impl.atlas.core.TileKindFactory;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DimensionUpdateS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "dimension", "update");

	public DimensionUpdateS2CPacket(int atlasID, RegistryKey<World> world, Collection<TileInfo> tiles) {
		this.writeVarInt(atlasID);
		this.writeIdentifier(world.getValue());
		this.writeVarInt(tiles.size());

		for (TileInfo tile : tiles) {
			this.writeShort(tile.x);
			this.writeShort(tile.z);
			this.writeInt(tile.biome.getId());
		}
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(PacketContext context, PacketByteBuf buf) {
		int atlasID = buf.readVarInt();
		RegistryKey<World> world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
		int tileCount = buf.readVarInt();

		if (world == null) {
			// TODO FABRIC
			return;
		}

		List<TileInfo> tiles = new ArrayList<>();
		for (int i = 0; i < tileCount; ++i) {
			tiles.add(new TileInfo(
					buf.readShort(),
					buf.readShort(),
					TileKindFactory.get(buf.readInt())
			));
		}

		context.getTaskQueue().execute(() -> {
			AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, context.getPlayer().world);

			for (TileInfo info : tiles) {
				data.getWorldData(world).setTile(info.x, info.z, info.biome);
			}
		});
	}
}
