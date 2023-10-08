package hunternif.mc.impl.atlas.forge;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import dev.architectury.utils.Env;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.client.gui.forge.AntiqueAtlasConfigMenu;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AntiqueAtlasMod.ID)
public class AntiqueAtlasModForge
{
    public AntiqueAtlasModForge()
    {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(AntiqueAtlasMod.ID,
                FMLJavaModLoadingContext.get().getModEventBus());

        AntiqueAtlasMod.init();

        if (Platform.getEnvironment() == Env.CLIENT)
        {
            AntiqueAtlasModClient.init();
            AntiqueAtlasConfigMenu.init();
        }
    }
}
