package hunternif.mc.impl.atlas.fabric;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.fabricmc.api.ModInitializer;

public class AntiqueAtlasModFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        AntiqueAtlasMod.init();
    }
}