package hunternif.mc.impl.atlas.client.gui.forge;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class AntiqueAtlasConfigMenu {
    public static void init() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> AutoConfig.getConfigScreen(AntiqueAtlasConfig.class, parent).get())
        );
    }
}
