package hunternif.mc.impl.atlas.fabric;

import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import net.fabricmc.api.ClientModInitializer;

public class AntiqueAtlasClientModFabric implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        AntiqueAtlasModClient.init();
    }
}