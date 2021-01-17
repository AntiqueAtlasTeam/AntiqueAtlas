package hunternif.mc.impl.atlas.network.packet.c2s;

import hunternif.mc.impl.atlas.network.packet.AntiqueAtlasPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public abstract class C2SPacket extends AntiqueAtlasPacket {
	public void send() {
		ClientPlayNetworking.send(this.getId(), this);
	}
}
