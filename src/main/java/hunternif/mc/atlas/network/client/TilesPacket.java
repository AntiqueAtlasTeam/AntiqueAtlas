package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 */
public class TilesPacket extends AbstractClientMessage<TilesPacket> {
	/** Size of one entry in the map in bytes. */
	private static final int ENTRY_SIZE_BYTES = 2 + 2 + 4;

	private RegistryKey<DimensionType> dimension;
	private int tileCount;
	private ByteBuf tileData;

	public TilesPacket() {}

	public TilesPacket(RegistryKey<DimensionType> dimension) {
		this.dimension = dimension;
		tileCount = 0;
		tileData = Unpooled.buffer();
	}

	public TilesPacket addTile(int x, int y, int biomeID) {
		tileData.writeShort(x);
		tileData.writeShort(y);
		tileData.writeInt(biomeID);
		tileCount++;
		return this;
	}

	public boolean isEmpty() {
		return tileCount == 0;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		dimension = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, buffer.readIdentifier());
		tileCount = buffer.readVarInt();
		tileData = buffer.readBytes(tileCount * ENTRY_SIZE_BYTES);
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeIdentifier(dimension.getValue());
		buffer.writeVarInt(tileCount);
		buffer.writeBytes(tileData);
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		if (dimension == null) {
			// TODO FABRIC
			return;
		}

		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		for (int i = 0; i < tileCount; i++) {
			data.setBiomeAt(dimension, tileData.readShort(), tileData.readShort(), tileData.readInt());
		}
	}
}
