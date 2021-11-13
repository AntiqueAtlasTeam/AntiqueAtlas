package hunternif.mc.impl.atlas.network.packet.s2c.play;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.forge.EntryPair;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 * @author Haven King
 */
public class CustomTileInfoS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "s2c", "custom_tile", "info");

	ResourceKey<Level> world;
	List<Map.Entry<ChunkPos, ResourceLocation>> tiles;
	
	public CustomTileInfoS2CPacket(ResourceKey<Level> world, List<Map.Entry<ChunkPos, ResourceLocation>> tiles) {
		this.world = world;
		this.tiles = tiles;
	}

	public CustomTileInfoS2CPacket(ResourceKey<Level> world, int chunkX, int chunkZ, ResourceLocation tileId) {
		this.world = world;
		this.tiles = Lists.newArrayList(new EntryPair<>(new ChunkPos(chunkX, chunkZ), tileId));
	}

	public static void encode(final CustomTileInfoS2CPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeResourceLocation(msg.world.location());
		packetBuffer.writeVarInt(msg.tiles.size());

		for (Map.Entry<ChunkPos, ResourceLocation> entry : msg.tiles) {
			packetBuffer.writeVarInt(entry.getKey().x);
			packetBuffer.writeVarInt(entry.getKey().z);
			packetBuffer.writeResourceLocation(entry.getValue());
		}
	}

	public static CustomTileInfoS2CPacket decode(final FriendlyByteBuf packetBuffer) {
		ResourceKey<Level> world = ResourceKey.create(Registry.DIMENSION_REGISTRY, packetBuffer.readResourceLocation());
		int tileCount = packetBuffer.readVarInt();

		List<Map.Entry<ChunkPos, ResourceLocation>> tiles = Lists.newArrayList();
		
		for (int i = 0; i < tileCount; ++i) {
			tiles.add(new EntryPair<>(new ChunkPos(packetBuffer.readVarInt(), packetBuffer.readVarInt()), packetBuffer.readResourceLocation()));
		}
		
		return new CustomTileInfoS2CPacket(world, tiles);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(LocalPlayer player) {
		TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(this.world);
		for (Map.Entry<ChunkPos, ResourceLocation> entry : this.tiles) {
			data.setTile(entry.getKey().x, entry.getKey().z, entry.getValue());
		}
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
