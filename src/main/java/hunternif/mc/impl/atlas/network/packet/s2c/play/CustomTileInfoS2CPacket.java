package hunternif.mc.impl.atlas.network.packet.s2c.play;

import java.util.Map;

import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 * @author Haven King
 */
public class CustomTileInfoS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "s2c", "custom_tile", "info");

	RegistryKey<World> world;
	Map<ChunkPos, ResourceLocation> tiles;
	
	public CustomTileInfoS2CPacket(RegistryKey<World> world, Map<ChunkPos, ResourceLocation> tiles) {
		this.world = world;
		this.tiles = tiles;
	}

	public CustomTileInfoS2CPacket(RegistryKey<World> world, int chunkX, int chunkZ, ResourceLocation tileId) {
		this.world = world;
		this.tiles = (new Builder<ChunkPos, ResourceLocation>()).put(new ChunkPos(chunkX, chunkZ), tileId).build();
	}

	public static void encode(final CustomTileInfoS2CPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeResourceLocation(msg.world.getLocation());
		packetBuffer.writeVarInt(msg.tiles.size());

		for (Map.Entry<ChunkPos, ResourceLocation> entry : msg.tiles.entrySet()) {
			packetBuffer.writeVarInt(entry.getKey().x);
			packetBuffer.writeVarInt(entry.getKey().z);
			packetBuffer.writeResourceLocation(entry.getValue());
		}
	}

	public static CustomTileInfoS2CPacket decode(final PacketBuffer packetBuffer) {
		RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, packetBuffer.readResourceLocation());
		int tileCount = packetBuffer.readVarInt();

		Map<ChunkPos, ResourceLocation> tiles = Maps.newHashMap();
		
		for (int i = 0; i < tileCount; ++i) {
			tiles.put(new ChunkPos(packetBuffer.readVarInt(), packetBuffer.readVarInt()), packetBuffer.readResourceLocation());
		}
		
		return new CustomTileInfoS2CPacket(world, tiles);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(ClientPlayerEntity player) {
		TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(this.world);
		for (Map.Entry<ChunkPos, ResourceLocation> entry : this.tiles.entrySet()) {
			data.setTile(entry.getKey().x, entry.getKey().z, entry.getValue());
		}
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

//	public static void apply(PacketContext context, PacketByteBuf buf) {
//		RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, buf.readIdentifier());
//		int tileCount = buf.readVarInt();
//
//		TileDataStorage data = AntiqueAtlasMod.tileData.getData(world);
//		for (int i = 0; i < tileCount; ++i) {
//			data.setTile(buf.readShort(), buf.readShort(), buf.readIdentifier());
//		}
//	}
}
