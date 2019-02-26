package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractServerMessage;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;

/**
 * A request from a client to create a new marker. In order to prevent griefing,
 * the marker has to be local.
 * @author Hunternif
 */
public class AddMarkerPacket extends AbstractServerMessage<AddMarkerPacket> {
	private int atlasID;
	private DimensionType dimension;
	private String type;
	private String label;
	private int x, y;
	private boolean visibleAhead;

	public AddMarkerPacket() {}

	/** Use this constructor when creating a <b>local</b> marker. */
	public AddMarkerPacket(int atlasID, DimensionType dimension, String type, String label, int x, int y, boolean visibleAhead) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.type = type;
		this.label = label;
		this.x = x;
		this.y = y;
		this.visibleAhead = visibleAhead;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = Registry.DIMENSION.get(buffer.readVarInt());
		type = buffer.readString(512);
		label = buffer.readString(512);
		x = buffer.readInt();
		y = buffer.readInt();
		visibleAhead = buffer.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeVarInt(Registry.DIMENSION.getRawId(dimension));
		buffer.writeString(type);
		buffer.writeString(label);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeBoolean(visibleAhead);
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		// Make sure it's this player's atlas :^)
		if (SettingsConfig.gameplay.itemNeeded && !AtlasAPI.getPlayerAtlases(player).contains(atlasID)) {
			Log.warn("Player %s attempted to put marker into someone else's Atlas #%d",
					player.getCommandSource().getName(), atlasID);
			return;
		}
		MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, player.getEntityWorld());
		Marker marker = markersData.createAndSaveMarker(type, label, dimension, x, y, visibleAhead);
		// If these are a manually set markers sent from the client, forward
		// them to other players. Including the original sender, because he
		// waits on the server to verify his marker.
		MarkersPacket packetForClients = new MarkersPacket(atlasID, dimension, marker);
		PacketDispatcher.sendToAll(((ServerWorld) player.getEntityWorld()).getServer(), packetForClients);
	}
}
