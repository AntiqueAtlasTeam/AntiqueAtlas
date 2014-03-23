package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.util.NetworkUtil;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;

/** Sends markers set via API from server to client, sends player-defined
 * markers from client to server. Only one dimension per packet.
 * @author Hunternif
 */
public class MarkersPacket extends CustomPacket {
	protected int atlasID;
	protected int dimension;
	protected final ListMultimap<String, Marker> markersByType = ArrayListMultimap.create();
	
	public MarkersPacket() {}
	
	public MarkersPacket(int atlasID, int dimension, Marker... markers) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		for (Marker marker : markers) {
			markersByType.put(marker.getType(), marker);
		}
	}
	
	public MarkersPacket putMarker(Marker marker) {
		markersByType.put(marker.getType(), marker);
		return this;
	}
	
	@Override
	public PacketDirection getPacketDirection() {
		return PacketDirection.BOTH;
	}

	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeShort(atlasID);
		out.writeShort(dimension);
		Set<String> types = markersByType.keySet();
		out.writeShort(types.size());
		for (String type : types) {
			out.writeUTF(type);
			List<Marker> markers = markersByType.get(type);
			out.writeShort(markers.size());
			for (Marker marker : markers) {
				out.writeUTF(marker.getLabel());
				out.writeInt(marker.getX());
				out.writeInt(marker.getY());
			}
		}
	}

	@Override
	public void read(ByteArrayDataInput in) throws ProtocolException {
		atlasID = in.readShort();
		dimension = in.readShort();
		int typesLength = in.readShort();
		for (int i = 0; i < typesLength; i++) {
			String type = in.readUTF();
			int markersLength = in.readShort();
			for (int j = 0; j < markersLength; j++) {
				Marker marker = new Marker(type, in.readUTF(), in.readInt(), in.readInt());
				markersByType.put(type, marker);
			}
		}
	}

	@Override
	public void execute(EntityPlayer player, Side side) throws ProtocolException {
		MarkersData markersData = side.isClient() ?
				AntiqueAtlasMod.itemAtlas.getClientMarkersData(atlasID) :
				AntiqueAtlasMod.itemAtlas.getMarkersData(atlasID, player.worldObj);
		for (Marker marker : markersByType.values()) {
			markersData.putMarker(dimension, marker);
		}
		if (side.isServer()) {
			markersData.markDirty();
			// If these are a manually set markers sent from the client, forward
			// them to other players. Including the original sender, because he
			// waits on the server to verify his marker.
			NetworkUtil.sendPacketToAllPlayersInWorld(player.worldObj, this.makePacket());
		}
	}
	
	public boolean isEmpty() {
		return markersByType.isEmpty();
	}

}
