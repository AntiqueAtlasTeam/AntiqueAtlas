package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractServerMessage;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

/**
 * A request from a client to create a new marker. In order to prevent griefing,
 * the marker has to be local.
 * @author Hunternif
 */
public class AddMarkerPacket extends AbstractServerMessage<AddMarkerPacket> {
	private int atlasID;
	private int dimension;
	private MarkerType type;
	private String label;
	private int x, y;
	private boolean visibleAhead;

	public AddMarkerPacket() {}

	/** Use this constructor when creating a <b>local</b> marker. */
	public AddMarkerPacket(int atlasID, int dimension, MarkerType type, String label, int x, int y, boolean visibleAhead) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.type = type;
		this.label = label;
		this.x = x;
		this.y = y;
		this.visibleAhead = visibleAhead;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = buffer.readVarInt();
		type = MarkerRegistry.find( ByteBufUtils.readUTF8String(buffer) );
		label = ByteBufUtils.readUTF8String(buffer);
		x = buffer.readInt();
		y = buffer.readInt();
		visibleAhead = buffer.readBoolean();
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeVarInt(dimension);
		ByteBufUtils.writeUTF8String(buffer, type.getRegistryName().toString());
		ByteBufUtils.writeUTF8String(buffer, label);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeBoolean(visibleAhead);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		// Make sure it's this player's atlas :^)
		if (!player.inventory.hasItemStack(new ItemStack(AntiqueAtlasMod.itemAtlas, 1, atlasID))) {
			Log.warn("Player %s attempted to put marker into someone else's Atlas #%d",
					player.getGameProfile().getName(), atlasID);
			return;
		}
		MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, player.getEntityWorld());
		Marker marker = markersData.createAndSaveMarker(type, label, dimension, x, y, visibleAhead);
		// If these are a manually set markers sent from the client, forward
		// them to other players. Including the original sender, because he
		// waits on the server to verify his marker.
		MarkersPacket packetForClients = new MarkersPacket(atlasID, dimension, marker);
		PacketDispatcher.sendToAll(packetForClients);
	}
}
