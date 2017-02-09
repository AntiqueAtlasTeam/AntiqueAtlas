package kenkron.antiqueatlasoverlay;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AntiqueAtlasOverlayMod.MODID, version = AntiqueAtlasOverlayMod.VERSION, name = AntiqueAtlasOverlayMod.MODID, dependencies = "required-after:antiqueatlas")
public class AntiqueAtlasOverlayMod
{
    public static final String MODID = "antiqueatlasoverlay";
    public static final String VERSION = "1.2";
    
    
    @SidedProxy(clientSide="kenkron.antiqueatlasoverlay.AAOClient", 
    		    serverSide="kenkron.antiqueatlasoverlay.AAOCommon")
    protected static AAOCommon proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	System.out.println(proxy.getClass());
    	proxy.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init(event);
    }
}
