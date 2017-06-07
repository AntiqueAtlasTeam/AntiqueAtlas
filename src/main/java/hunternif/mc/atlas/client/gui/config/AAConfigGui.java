package hunternif.mc.atlas.client.gui.config;

import com.google.common.collect.Lists;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class AAConfigGui extends GuiConfig {

    public AAConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getElements(), AntiqueAtlasMod.ID, false, false, AntiqueAtlasMod.NAME);
    }

    public static List<IConfigElement> getElements() {
        return Lists.newArrayList(
                new ConfigElement(AntiqueAtlasMod.settings.config.getCategory(SettingsConfig.GAMEPLAY)),
                new ConfigElement(AntiqueAtlasMod.settings.config.getCategory(SettingsConfig.INTERFACE)),
                new ConfigElement(AntiqueAtlasMod.settings.config.getCategory(SettingsConfig.PERFORMANCE))
        );
    }
}
