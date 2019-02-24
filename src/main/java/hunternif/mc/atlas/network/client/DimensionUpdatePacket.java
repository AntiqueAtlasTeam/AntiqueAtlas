package hunternif.mc.atlas.network.client;

import java.io.IOException;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.TileKind;
import hunternif.mc.atlas.core.TileKindFactory;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;


public class DimensionUpdatePacket extends AbstractClientMessage<DimensionUpdatePacket>{
	/** Size of one entry in the map in bytes. */
	private static final int ENTRY_SIZE_BYTES = 2 + 2 + 4;

	private int atlasID;
	private DimensionType dimension;
	private int tileCount;
	private ByteBuf tileData;

	public DimensionUpdatePacket() {}

	public DimensionUpdatePacket(int atlasID, DimensionType dimension) {
		this.dimension = dimension;
		tileCount = 0;
		tileData = Unpooled.buffer();
	}

	public DimensionUpdatePacket addTile(int x, int y, TileKind biomeID) {
		tileData.writeShort(x);
		tileData.writeShort(y);
		tileData.writeInt(biomeID.getId());
		tileCount++;
		return this;
	}

	public boolean isEmpty() {
		return tileCount == 0;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = Registry.DIMENSION.get(buffer.readIdentifier());
		tileCount = buffer.readVarInt();
		tileData = buffer.readBytes(tileCount * ENTRY_SIZE_BYTES);
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeIdentifier(Registry.DIMENSION.getId(dimension));
		buffer.writeVarInt(tileCount);
		buffer.writeBytes(tileData);
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		if (dimension == null) {
			// TODO FABRIC
			return;
		}

		AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.world);
		for (int i = 0; i < tileCount; i++) {
			data.getDimensionData(dimension).setTile(tileData.readShort(), tileData.readShort(), TileKindFactory.get(tileData.readInt()));
		}
	}
}
