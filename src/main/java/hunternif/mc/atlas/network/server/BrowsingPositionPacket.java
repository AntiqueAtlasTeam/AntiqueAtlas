package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.network.AbstractMessage.AbstractServerMessage;
import hunternif.mc.atlas.util.Log;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.relauncher.Side;

/**
 * Packet used to save the last browsing position for a dimension in an atlas.
 * @author Hunternif
 */
public class BrowsingPositionPacket extends AbstractServerMessage<BrowsingPositionPacket> {
	public static final double ZOOM_SCALE_FACTOR = 128;
	
	private int atlasID;
	private int dimension;
	private int x, y;
	private double zoom;
	
	public BrowsingPositionPacket() {}
	
	public BrowsingPositionPacket(int atlasID, int dimension, int x, int y, double zoom) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}
	
	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarIntFromBuffer();
		dimension = buffer.readVarIntFromBuffer();
		x = buffer.readVarIntFromBuffer();
		y = buffer.readVarIntFromBuffer();
		zoom = (double)buffer.readVarIntFromBuffer() / ZOOM_SCALE_FACTOR;
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarIntToBuffer(atlasID);
		buffer.writeVarIntToBuffer(dimension);
		buffer.writeVarIntToBuffer(x);
		buffer.writeVarIntToBuffer(y);
		buffer.writeVarIntToBuffer((int)Math.round(zoom * ZOOM_SCALE_FACTOR));
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		// Make sure it's this player's atlas :^)
		if (!player.inventory.hasItemStack(new ItemStack(AntiqueAtlasMod.itemAtlas, 1, atlasID))) {
			Log.warn("Player %s attempted to put marker into someone else's Atlas #%d",
					player.getGameProfile().getName(), atlasID);
			return;
		}
		AntiqueAtlasMod.itemAtlas.getAtlasData(atlasID, player.worldObj)
			.getDimensionData(dimension).setBrowsingPosition(x, y, zoom);
	}

}
