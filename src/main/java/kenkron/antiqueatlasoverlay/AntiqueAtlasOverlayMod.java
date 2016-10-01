package kenkron.antiqueatlasoverlay;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AntiqueAtlasOverlayMod.MODID, version = AntiqueAtlasOverlayMod.VERSION, name = AntiqueAtlasOverlayMod.MODID, dependencies = "required-after:antiqueatlas")
public class AntiqueAtlasOverlayMod
{
    public static final String MODID = "AntiqueAtlasOverlay";
    public static final String VERSION = "1.2";
    
    AAORenderEventReceiver renderer;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	renderer = new AAORenderEventReceiver();
    	AAOConfig.load(new File(event.getModConfigurationDirectory(), "AntiqueAtlasOverlay.cfg"), renderer);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(renderer);
    }
}
