package hunternif.mc.impl.atlas.network.packet.c2s;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.AntiqueAtlasPacket;

public abstract class C2SPacket extends AntiqueAtlasPacket {
	public void send() {
//		ClientSidePacketRegistry.INSTANCE.sendToServer(this.getId(), this);
		AntiqueAtlasMod.MOD_CHANNEL.sendToServer(this);
	}
}
