package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Sent from server to client to remove a custom global tile.
 * @author Hunternif
 * @author Haven King
 */
public class DeleteCustomGlobalTileS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "delete");

	ResourceKey<Level> world;
	int chunkX, chunkZ;

	public DeleteCustomGlobalTileS2CPacket(ResourceKey<Level> world, int chunkX, int chunkZ) {
		this.world = world;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public static void encode(final DeleteCustomGlobalTileS2CPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeResourceLocation(msg.world.location());
		packetBuffer.writeVarInt(msg.chunkX);
		packetBuffer.writeVarInt(msg.chunkZ);
	}

	public static DeleteCustomGlobalTileS2CPacket decode(final FriendlyByteBuf packetBuffer) {
		ResourceKey<Level> world = ResourceKey.create(Registry.DIMENSION_REGISTRY, packetBuffer.readResourceLocation());
		int chunkX = packetBuffer.readVarInt();
		int chunkZ = packetBuffer.readVarInt();

		return new DeleteCustomGlobalTileS2CPacket(world, chunkX, chunkZ);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(LocalPlayer player) {
		TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(this.world);
		data.removeTile(this.chunkX, this.chunkZ);
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
