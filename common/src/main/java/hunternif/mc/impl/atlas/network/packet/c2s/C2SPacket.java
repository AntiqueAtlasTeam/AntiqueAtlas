package hunternif.mc.impl.atlas.network.packet.c2s;

import me.shedaniel.architectury.networking.simple.BaseC2SMessage;

public abstract class C2SPacket extends BaseC2SMessage {
    public void send() {
        sendToServer();
    }
}
