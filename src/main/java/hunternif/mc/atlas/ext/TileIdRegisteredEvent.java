package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.network.client.TileNameIDPacket;

import java.util.Map;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is dispatched on the client when it receives a
 * {@link TileNameIDPacket} with registered tile ids.
 * @author Hunternif
 */
public class TileIdRegisteredEvent extends Event {
	public final Map<String, Integer> nameToIdMap;
	
	public TileIdRegisteredEvent(Map<String, Integer> nameToIdMap) {
		super();
		this.nameToIdMap = nameToIdMap;
	}
}
