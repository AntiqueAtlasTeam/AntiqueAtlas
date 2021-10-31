package hunternif.mc.impl.atlas.client.gui;

import java.util.function.Supplier;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmlclient.ConfigGuiHandler.ConfigGuiFactory;

@OnlyIn(Dist.CLIENT)
public class AntiqueAtlasModMenu {
	
    public static Supplier<ConfigGuiFactory> getModConfigScreenFactory() {
			return () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, parentScreen) -> {
				return AutoConfig.getConfigScreen(AntiqueAtlasConfig.class, parentScreen).get();
			});
    }
}
