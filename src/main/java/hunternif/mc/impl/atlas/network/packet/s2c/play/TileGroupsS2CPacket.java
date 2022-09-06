package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.WorldData;
import hunternif.mc.impl.atlas.core.TileGroup;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


/**
 * Syncs tile groups to the client.
 * @author Hunternif
 * @author Haven King
 */
public class TileGroupsS2CPacket extends S2CPacket {
	public static final int TILE_GROUPS_PER_PACKET = 100;
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "tile", "groups");

	public TileGroupsS2CPacket(int atlasID, RegistryKey<World> world, List<TileGroup> tileGroups) {
		this.writeVarInt(atlasID);
		this.writeIdentifier(world.getValue());
		this.writeVarInt(tileGroups.size());

		for (TileGroup tileGroup : tileGroups) {
			this.writeNbt(tileGroup.writeToNBT(new NbtCompound()));
		}
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		int atlasID = buf.readVarInt();
		RegistryKey<World> world = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
		int length = buf.readVarInt();
		List<TileGroup> tileGroups = new ArrayList<>(length);

		for (int i = 0; i < length; ++i) {
			NbtCompound tag = buf.readNbt();

			if (tag != null) {
				tileGroups.add(TileGroup.fromNBT(tag));
			}
		}

		client.execute(() -> {
			assert client.player != null;
			AtlasData atlasData = AntiqueAtlasMod.tileData.getData(atlasID, client.player.getEntityWorld());
			WorldData dimData = atlasData.getWorldData(world);
			for (TileGroup t : tileGroups) {
				dimData.putTileGroup(t);
			}
		});
	}
}
