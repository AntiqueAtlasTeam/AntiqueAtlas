package hunternif.mc.atlas.network.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.event.OptionalMarkerEvent;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Sends markers set via API from server to client.
 * Only one dimension per packet.
 * The markers in one packet are either all global or all local.
 * @author Hunternif
 */
public class MarkersPacket extends AbstractClientMessage<MarkersPacket> {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;
	private int atlasID;
	private int dimension;
	private final ListMultimap<String, Marker> markersByType = ArrayListMultimap.create();

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

	private boolean isGlobal() {
		return atlasID == GLOBAL;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = buffer.readVarInt();
		int typesLength = buffer.readVarInt();
		for (int i = 0; i < typesLength; i++) {
			String type = ByteBufUtils.readUTF8String(buffer);
			int markersLength = buffer.readVarInt();
			for (int j = 0; j < markersLength; j++) {
				Marker marker = new Marker(buffer.readVarInt(),
						type, ByteBufUtils.readUTF8String(buffer),
						dimension, buffer.readInt(), buffer.readInt(),
						buffer.readBoolean());
				markersByType.put(type, marker);
			}
		}
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeVarInt(dimension);
		Set<String> types = markersByType.keySet();
		buffer.writeVarInt(types.size());
		for (String type : types) {
			ByteBufUtils.writeUTF8String(buffer, type);
			List<Marker> markers = markersByType.get(type);
			buffer.writeVarInt(markers.size());
			for (Marker marker : markers) {
				buffer.writeVarInt(marker.getId());
				ByteBufUtils.writeUTF8String(buffer, marker.getLabel());
				buffer.writeInt(marker.getX());
				buffer.writeInt(marker.getZ());
				buffer.writeBoolean(marker.isVisibleAhead());
			}
		}
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		if (isGlobal()) {
			MarkersData globalMarkersData = AntiqueAtlasMod.globalMarkersData.getData();
			for (Marker marker : markersByType.values()) {
				MarkerType type = MarkerRegistry.find(marker.getType());
				if (type == null) continue;
				if (!MinecraftForge.EVENT_BUS.post(new OptionalMarkerEvent(
						GLOBAL, dimension, type, marker.getLabel(),
						marker.getX(), marker.getZ(), marker.isVisibleAhead()
				))) {
					globalMarkersData.loadMarker(marker);
					// Load this marker into all currently loaded atlases,
					// this will only happen when a global marker is added mid-game.
					for (MarkersData markersData : AntiqueAtlasMod.markersData.getAllLoadedMarkersData()) {
						// flip the id so that it doesn't conflict with the local markers:
						Marker localCopy = new Marker(
								-marker.getId(),
								marker.getType(), marker.getLabel(),
								marker.getDimension(),
								marker.getX(), marker.getZ(),
								marker.isVisibleAhead()
						);
						markersData.loadMarker(localCopy);
					}
				}
			}
		} else {
			MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, player.getEntityWorld());
			for (Marker marker : markersByType.values()) {
				markersData.loadMarker(marker);
			}
		}

	}
}
