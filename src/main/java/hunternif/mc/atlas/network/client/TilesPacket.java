package hunternif.mc.atlas.network.client;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface TilesPacket extends IMessage {
    void addTile(int x, int y, int biomeID);
}
