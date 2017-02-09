package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.util.Log;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Deletes a marker. A client sends this packet to the server as a request,
 * and the server sends it back to all players as a response, including the
 * original sender.
 * @author Hunternif
 */
public class DeleteMarkerPacket extends AbstractMessage<DeleteMarkerPacket> {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;
	private int atlasID;
	private int markerID;

	public DeleteMarkerPacket() {}

	/** Use this constructor when deleting a <b>local</b> marker. */
	public DeleteMarkerPacket(int atlasID, int markerID) {
		this.atlasID = atlasID;
		this.markerID = markerID;
	}

	/** Use this constructor when deleting a <b>global</b> marker. */
	public DeleteMarkerPacket(int markerID) {
		this(GLOBAL, markerID);
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarInt();
		markerID = buffer.readVarInt();
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarIntToBuffer(atlasID);
		buffer.writeVarIntToBuffer(markerID);
	}

	private boolean isGlobal() {
		return atlasID == GLOBAL;
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			// Make sure it's this player's atlas :^)
			if (side.isServer() && !player.inventory.hasItemStack(new ItemStack(AntiqueAtlasMod.itemAtlas, 1, atlasID))) {
				Log.warn("Player %s attempted to delete marker from someone else's Atlas #%d",
						player.getGameProfile().getName(), atlasID);
				return;
			}
			if (isGlobal()) {
				AtlasAPI.markers.deleteGlobalMarker(player.getEntityWorld(), markerID);
			} else {
				AtlasAPI.markers.deleteMarker(player.getEntityWorld(), atlasID, markerID);
			}
		} else {
			MarkersData data = isGlobal() ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.markersData.getMarkersData(atlasID, player.getEntityWorld());
					data.removeMarker(markerID);
		}
	}
}
