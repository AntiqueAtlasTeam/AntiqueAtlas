package hunternif.mc.atlas.network.client;

import java.io.IOException;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class DimensionUpdatePacket extends AbstractClientMessage<DimensionUpdatePacket>{
	/** Size of one entry in the map in bytes. */
	private static final int ENTRY_SIZE_BYTES = 2 + 2 + 2;

	private int atlasID;
	private int dimension;
	private int tileCount;
	private ByteBuf tileData;

	public DimensionUpdatePacket() {}

	public DimensionUpdatePacket(int atlasID, int dimension) {
		this.dimension = dimension;
		tileCount = 0;
		tileData = Unpooled.buffer();
	}

	public DimensionUpdatePacket addTile(int x, int y, int biomeID) {
		tileData.writeShort(x);
		tileData.writeShort(y);
		tileData.writeShort(biomeID);
		tileCount++;
		return this;
	}

	public boolean isEmpty() {
		return tileCount == 0;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = buffer.readVarInt();
		tileCount = buffer.readVarInt();
		tileData = buffer.readBytes(tileCount * ENTRY_SIZE_BYTES);
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeVarInt(dimension);
		buffer.writeVarInt(tileCount);
		buffer.writeBytes(tileData);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.world);
		for (int i = 0; i < tileCount; i++) {
			data.getDimensionData(dimension).setTile(tileData.readShort(), tileData.readShort(), new Tile(tileData.readShort()));
		}
	}
}
