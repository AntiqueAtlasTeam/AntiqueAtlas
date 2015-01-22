package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.AbstractMessage.AbstractServerMessage;
import hunternif.mc.atlas.network.client.TileNameIDPacket;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;

/**
 * Sent from client to server to register a new
 * (unique tile name)-(pseudo-biome ID) pair.
 * @author Hunternif
 */
public class RegisterTileIdPacket extends AbstractServerMessage<RegisterTileIdPacket> {
	private String name;
	
	public RegisterTileIdPacket() {}
	
	public RegisterTileIdPacket(String uniqueTileName) {
		this.name = uniqueTileName;
	}
	
	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		name = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		ByteBufUtils.writeUTF8String(buffer, name);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		// Register the new tile id:
		int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(name);
		// Send it to all clients:
		TileNameIDPacket packet = new TileNameIDPacket();
		packet.put(name, biomeID);
		PacketDispatcher.sendToAll(packet);
	}
	
}
