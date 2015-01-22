package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;

/**
 * Sends markers set via API from server to client.
 * Only one dimension per packet.
 * The markers in one packet are either all global or all local.
 * @author Hunternif
 */
public class MarkersPacket extends AbstractClientMessage<MarkersPacket> {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;
	protected int atlasID;
	protected int dimension;
	protected final ListMultimap<String, Marker> markersByType = ArrayListMultimap.create();

	public MarkersPacket() {}

	/** Use this constructor when creating a <b>local</b> marker. */
	public MarkersPacket(int atlasID, int dimension, Marker... markers) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		for (Marker marker : markers) {
			markersByType.put(marker.getType(), marker);
		}
	}

	/** Use this constructor when creating a <b>global</b> marker. */
	public MarkersPacket(int dimension, Marker... markers) {
		this(GLOBAL, dimension, markers);
	}

	public MarkersPacket putMarker(Marker marker) {
		markersByType.put(marker.getType(), marker);
		return this;
	}

	public boolean isEmpty() {
		return markersByType.isEmpty();
	}

	public boolean isGlobal() {
		return atlasID == GLOBAL;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readShort();
		dimension = buffer.readShort();
		int typesLength = buffer.readShort();
		for (int i = 0; i < typesLength; i++) {
			String type = ByteBufUtils.readUTF8String(buffer);
			int markersLength = buffer.readShort();
			for (int j = 0; j < markersLength; j++) {
				Marker marker = new Marker(buffer.readInt(),
						type, ByteBufUtils.readUTF8String(buffer),
						dimension, buffer.readInt(), buffer.readInt(),
						buffer.readBoolean());
				markersByType.put(type, marker);
			}
		}
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeShort(atlasID);
		buffer.writeShort(dimension);
		Set<String> types = markersByType.keySet();
		buffer.writeShort(types.size());
		for (String type : types) {
			ByteBufUtils.writeUTF8String(buffer, type);
			List<Marker> markers = markersByType.get(type);
			buffer.writeShort(markers.size());
			for (Marker marker : markers) {
				buffer.writeInt(marker.getId());
				ByteBufUtils.writeUTF8String(buffer, marker.getLabel());
				buffer.writeInt(marker.getX());
				buffer.writeInt(marker.getZ());
				buffer.writeBoolean(marker.isVisibleAhead());
			}
		}
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		MarkersData markersData = isGlobal() ?
				AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.itemAtlas.getMarkersData(atlasID, player.worldObj);
		for (Marker marker : markersByType.values()) {
			markersData.loadMarker(marker);
		}
	}
}
