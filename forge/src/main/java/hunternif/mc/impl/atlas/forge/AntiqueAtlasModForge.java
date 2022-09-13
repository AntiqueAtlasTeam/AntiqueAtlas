package hunternif.mc.impl.atlas.forge;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.client.gui.forge.AntiqueAtlasConfigMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import me.shedaniel.architectury.platform.forge.EventBuses;
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
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> AntiqueAtlasModClient::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> AntiqueAtlasConfigMenu::init);
    }
}
