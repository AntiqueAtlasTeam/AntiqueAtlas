package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Sent from server to client to remove a custom global tile.
 * @author Hunternif
 * @author Haven King
 */
public class DeleteCustomGlobalTileS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "delete");

	RegistryKey<World> world;
	int chunkX, chunkZ;

	public DeleteCustomGlobalTileS2CPacket(RegistryKey<World> world, int chunkX, int chunkZ) {
		this.world = world;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public static void encode(final DeleteCustomGlobalTileS2CPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeResourceLocation(msg.world.getLocation());
		packetBuffer.writeVarInt(msg.chunkX);
		packetBuffer.writeVarInt(msg.chunkZ);
	}

	public static DeleteCustomGlobalTileS2CPacket decode(final PacketBuffer packetBuffer) {
		RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, packetBuffer.readResourceLocation());
		int chunkX = packetBuffer.readVarInt();
		int chunkZ = packetBuffer.readVarInt();

		return new DeleteCustomGlobalTileS2CPacket(world, chunkX, chunkZ);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(ClientPlayerEntity player) {
		TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(this.world);
		data.removeTile(this.chunkX, this.chunkZ);
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	//	public static void apply(PacketContext context, PacketByteBuf buf) {
	//		RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, buf.readIdentifier());
	//		int chunkX = buf.readVarInt();
	//		int chunkZ = buf.readVarInt();
	//
	//		context.getTaskQueue().execute(() -> {
	//			TileDataStorage data = AntiqueAtlasMod.tileData.getData(world);
	//			data.removeTile(chunkX, chunkZ);
	//		});
	//	}
}
