package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
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
 * Puts biome tile into one atlas.
 * @author Hunternif
 * @author Haven King
 */
public class PutTileS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "s2c", "tile", "put");

	int atlasID;
	RegistryKey<World> world;
	int x, z;
	ResourceLocation tile;

	public PutTileS2CPacket(int atlasID, RegistryKey<World> world, int x, int z, ResourceLocation tile) {

	}

	public static void encode(final PutTileS2CPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.atlasID);
		packetBuffer.writeResourceLocation(msg.world.getLocation());
		packetBuffer.writeVarInt(msg.x);
		packetBuffer.writeVarInt(msg.z);
		packetBuffer.writeResourceLocation(msg.tile);
	}

	public static PutTileS2CPacket decode(final PacketBuffer packetBuffer) {
		int atlasID = packetBuffer.readVarInt();
		RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, packetBuffer.readResourceLocation());
		int x = packetBuffer.readVarInt();
		int z = packetBuffer.readVarInt();
		ResourceLocation tile = packetBuffer.readResourceLocation();

		return new PutTileS2CPacket(atlasID, world, x, z, tile);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean handle(ClientPlayerEntity player) {
		AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(this.atlasID, player.getEntityWorld());
		data.setTile(this.world, this.x, this.z, this.tile);
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	//	public static void apply(PacketContext context, PacketByteBuf buf) {
	//		int atlasID = buf.readVarInt();
	//		RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, buf.readIdentifier());
	//		int x = buf.readVarInt();
	//		int z = buf.readVarInt();
	//		ResourceLocation tile = buf.readIdentifier();
	//
	//		context.getTaskQueue().execute(() -> {
	//			AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, context.getPlayer().getEntityWorld());
	//			data.setTile(world, x, z, tile);
	//		});
	//	}
}
