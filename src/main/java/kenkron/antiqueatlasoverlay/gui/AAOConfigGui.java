package kenkron.antiqueatlasoverlay.gui;

import com.google.common.collect.Lists;
import kenkron.antiqueatlasoverlay.AAOConfig;
import kenkron.antiqueatlasoverlay.AntiqueAtlasOverlayMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class AAOConfigGui extends GuiConfig {

    public AAOConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getElements(), AntiqueAtlasOverlayMod.MODID, false, false, AntiqueAtlasOverlayMod.MODID);
    }

    public static List<IConfigElement> getElements() {
        return Lists.newArrayList(
                new ConfigElement(AAOConfig.config.getCategory(AAOConfig.APPEARANCE)),
                new ConfigElement(AAOConfig.config.getCategory(AAOConfig.POSITION))
        );
    }
}
