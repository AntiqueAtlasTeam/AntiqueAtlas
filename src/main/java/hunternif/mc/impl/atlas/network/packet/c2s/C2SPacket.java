package hunternif.mc.impl.atlas.network.packet.c2s;

import hunternif.mc.impl.atlas.network.packet.AntiqueAtlasPacket;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public abstract class C2SPacket extends AntiqueAtlasPacket {
	public void send() {
		ClientSidePacketRegistry.INSTANCE.sendToServer(this.getId(), this);
	}
}
