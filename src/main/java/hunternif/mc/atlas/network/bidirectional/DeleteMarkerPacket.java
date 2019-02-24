package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

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
	public void read(PacketByteBuf buffer) throws IOException {
		atlasID = buffer.readVarInt();
		markerID = buffer.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeVarInt(markerID);
	}

	private boolean isGlobal() {
		return atlasID == GLOBAL;
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		if (side == EnvType.SERVER) {
			// Make sure it's this player's atlas :^)
			// TODO FABRIC
			if (SettingsConfig.gameplay.itemNeeded /* && !player.bB.h(new ata(RegistrarAntiqueAtlas.ATLAS, 1, atlasID)) */) {
				Log.warn("Player %s attempted to delete marker from someone else's Atlas #%d",
						player.getCommandSource().getName(), atlasID);
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
