package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;

/**
 * Used to sync a single tile from server to client. Required to display
 * villages on the client.
 * @author Hunternif
 */
public class TilePacket extends CustomPacket {

	private int atlasID, dimension, biomeID;
	private ShortVec2 coords;
	
	public TilePacket() {}
	
	public TilePacket(int atlasID, int dimension, ShortVec2 coords, int biomeID) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.coords = coords;
		this.biomeID = biomeID;
	}
	
	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeShort(atlasID);
		out.writeShort(dimension);
		out.writeShort(coords.x);
		out.writeShort(coords.y);
		out.writeShort(biomeID);
	}

	@Override
	public void read(ByteArrayDataInput in) throws ProtocolException {
		atlasID = in.readShort();
		dimension = in.readShort();
		coords = new ShortVec2(in.readShort(), in.readShort());
		biomeID = in.readShort();
	}

	@Override
	public void execute(EntityPlayer player, Side side) throws ProtocolException {
		if (side.isClient()) {
			AtlasData atlasData = AntiqueAtlasMod.itemAtlas.getClientAtlasData(atlasID);
			atlasData.putTile(dimension, coords, new MapTile(biomeID));
		} else {
			throw new ProtocolException("Cannot send TilePacket to the server!");
		}
	}

}
