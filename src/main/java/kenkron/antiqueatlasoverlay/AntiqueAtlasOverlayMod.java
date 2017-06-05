package kenkron.antiqueatlasoverlay;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import java.io.File;

@Mod(modid = AntiqueAtlasOverlayMod.MODID, version = AntiqueAtlasOverlayMod.VERSION, name = AntiqueAtlasOverlayMod.MODID, clientSideOnly = true, dependencies = "required-after:antiqueatlas")
public class AntiqueAtlasOverlayMod {
    public static final String MODID = "antiqueatlasoverlay";
    public static final String VERSION = "1.2";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        AAORenderEventReceiver renderer = new AAORenderEventReceiver();
        AAOConfig.load(new File(AntiqueAtlasMod.proxy.getConfigDir(), "/AntiqueAtlasOverlay.cfg"), renderer);
        MinecraftForge.EVENT_BUS.register(renderer);
    }
}
