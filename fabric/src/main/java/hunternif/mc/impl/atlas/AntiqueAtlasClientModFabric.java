package hunternif.mc.impl.atlas;

import net.fabricmc.api.ClientModInitializer;

public class AntiqueAtlasClientModFabric implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        AntiqueAtlasModClient.init();
    }
}
