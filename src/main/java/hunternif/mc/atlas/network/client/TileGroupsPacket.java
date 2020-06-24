package hunternif.mc.atlas.network.client;

import java.io.IOException;
import java.util.ArrayList;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.core.TileGroup;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;


public class TileGroupsPacket extends AbstractClientMessage<TileGroupsPacket> {

	public int atlasID;
	public DimensionType dimension;
	public ArrayList<TileGroup> tileGroups;

	public static final int TILE_GROUPS_PER_PACKET = 100;
	
	public TileGroupsPacket(){
		tileGroups = new ArrayList<TileGroup>();
	}
	
	public TileGroupsPacket(ArrayList<TileGroup> tileGroups, int atlasID, DimensionType dimension) {
		this.tileGroups = tileGroups;
		this.atlasID = atlasID;
		this.dimension = dimension;
	}

	@Override
	protected void read(PacketByteBuf buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = Registry.DIMENSION_TYPE.get(buffer.readVarInt());
		int length = buffer.readVarInt();
		tileGroups = new ArrayList<TileGroup>(length);
		for (int i = 0; i < length; i++) {
			TileGroup newbie = new TileGroup(0, 0);
			newbie.readFromNBT(buffer.readCompoundTag());
			tileGroups.add(newbie);
		}
	}

	@Override
	protected void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeVarInt(Registry.DIMENSION_TYPE.getRawId(dimension));
		buffer.writeVarInt(tileGroups.size());
		for (TileGroup t : tileGroups) {
			CompoundTag me = new CompoundTag();
			t.writeToNBT(me);
			buffer.writeCompoundTag(me);
		}
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.world);
		DimensionData dimData = atlasData.getDimensionData(dimension);
		for (TileGroup t : tileGroups) {
			dimData.putTileGroup(t);
		}
	}
}
