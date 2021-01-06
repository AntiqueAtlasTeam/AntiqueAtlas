package hunternif.mc.api.client;

import hunternif.mc.impl.atlas.api.MarkerAPI;
import hunternif.mc.impl.atlas.api.client.impl.MarkerApiImplClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MarkerRegistry {
	public static MarkerAPI API = new MarkerApiImplClient();
}
