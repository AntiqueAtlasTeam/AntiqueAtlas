package hunternif.mc.atlas.core;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet131MapData;
import cpw.mods.fml.common.network.ITinyPacketHandler;

public class MapPacketHandler implements ITinyPacketHandler {

	@Override
	public void handle(NetHandler handler, Packet131MapData mapData) {
		int dimension = mapData.uniqueID;
		
	}

}
