package hunternif.mc.api.client;

import hunternif.mc.impl.atlas.api.MarkerAPI;
import hunternif.mc.impl.atlas.api.client.impl.MarkerApiImplClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MarkerRegistry {
	public static MarkerAPI API = new MarkerApiImplClient();
}
