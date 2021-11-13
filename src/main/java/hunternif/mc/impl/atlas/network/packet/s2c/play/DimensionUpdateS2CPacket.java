package hunternif.mc.impl.atlas.network.packet.s2c.play;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileInfo;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DimensionUpdateS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "s2c", "dimension", "update");

	int atlasID;
	ResourceKey<Level> world;
	Collection<TileInfo> tiles;

	public DimensionUpdateS2CPacket(int atlasID, ResourceKey<Level> world, Collection<TileInfo> tiles) {
		this.atlasID = atlasID;
		this.world = world;
		this.tiles = tiles;
	}

	public static void encode(final DimensionUpdateS2CPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeResourceLocation(msg.world.location());
		packetBuffer.writeVarInt(msg.tiles.size());

		for (TileInfo tile : msg.tiles) {
			packetBuffer.writeVarInt(tile.x);
			packetBuffer.writeVarInt(tile.z);
			packetBuffer.writeResourceLocation(tile.id);
		}
	}

	public static DimensionUpdateS2CPacket decode(final FriendlyByteBuf packetBuffer) {
		int atlasID = packetBuffer.readVarInt();
		ResourceKey<Level> world = ResourceKey.create(Registry.DIMENSION_REGISTRY, packetBuffer.readResourceLocation());
		int tileCount = packetBuffer.readVarInt();

		List<TileInfo> tiles = new ArrayList<>();
		for (int i = 0; i < tileCount; ++i) {
			tiles.add(new TileInfo(
					packetBuffer.readVarInt(),
					packetBuffer.readVarInt(),
					packetBuffer.readResourceLocation())
					);
		}

		return new DimensionUpdateS2CPacket(atlasID, world, tiles);
	}

	@Override
	public boolean shouldRun() {
		return this.world != null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(LocalPlayer player) {
		AtlasData data = AntiqueAtlasMod.tileData.getData(this.atlasID, player.level);
		for (TileInfo info : this.tiles) {
			data.getWorldData(this.world).setTile(info.x, info.z, info.id);
		}
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
