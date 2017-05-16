package hunternif.mc.atlas.network.client;

import java.io.IOException;
import java.util.ArrayList;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.core.TileGroup;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class TileGroupsPacket extends AbstractClientMessage<TileGroupsPacket> {

	public int atlasID, dimension;
	public ArrayList<TileGroup> tileGroups;

	public static final int TILE_GROUPS_PER_PACKET = 100;
	
	public TileGroupsPacket(){
		tileGroups = new ArrayList<TileGroup>();
	}
	
	public TileGroupsPacket(ArrayList<TileGroup> tileGroups, int atlasID, int dimension) {
		this.tileGroups = tileGroups;
		this.atlasID = atlasID;
		this.dimension = dimension;
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = buffer.readVarInt();
		int length = buffer.readVarInt();
		tileGroups = new ArrayList<TileGroup>(length);
		for (int i = 0; i < length; i++) {
			TileGroup newbie = new TileGroup(0, 0);
			newbie.readFromNBT(ByteBufUtils.readTag(buffer));
			tileGroups.add(newbie);
		}
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeVarInt(dimension);
		buffer.writeVarInt(tileGroups.size());
		for (TileGroup t : tileGroups) {
			NBTTagCompound me = new NBTTagCompound();
			t.writeToNBT(me);
			ByteBufUtils.writeTag(buffer, me);
		}
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.world);
		DimensionData dimData = atlasData.getDimensionData(dimension);
		for (TileGroup t : tileGroups) {
			dimData.putTileGroup(t);
		}
	}
}
