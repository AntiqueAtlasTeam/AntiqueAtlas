package hunternif.mc.atlas;

import hunternif.mc.atlas.core.PlayerTracker;
import hunternif.mc.atlas.network.CustomPacketHandler;

import java.util.logging.Logger;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid=AntiqueAtlasMod.ID, name=AntiqueAtlasMod.NAME, version=AntiqueAtlasMod.VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, packetHandler=CustomPacketHandler.class, channels={AntiqueAtlasMod.CHANNEL})
public class AntiqueAtlasMod {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";
	public static final String VERSION = "@@MOD_VERSION@@";
	public static final String CHANNEL = ID;
	
	@Instance(ID)
	public static AntiqueAtlasMod instance;
	
	public static Logger logger;
	
	@SidedProxy(clientSide="hunternif.mc.atlas.ClientProxy", serverSide="hunternif.mc.atlas.CommonProxy")
	public static CommonProxy proxy;
	
	public static PlayerTracker playerTracker = new PlayerTracker();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
		MinecraftForge.EVENT_BUS.register(playerTracker);
		GameRegistry.registerPlayerTracker(playerTracker);
		KeyBindingRegistry.registerKeyBinding(new AtlasKeyHandler());
		LanguageRegistry.instance().addStringLocalization(AtlasKeyHandler.KEY_DESCRIPTION_ATLAS, "en_US", "Antique Atlas");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
