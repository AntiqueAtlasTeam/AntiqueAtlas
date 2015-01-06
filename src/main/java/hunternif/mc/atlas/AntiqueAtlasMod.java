package hunternif.mc.atlas;

import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;
import hunternif.mc.atlas.ext.ExtBiomeDataHandler;
import hunternif.mc.atlas.ext.VillageWatcher;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;
import hunternif.mc.atlas.item.RecipeAtlasCloning;
import hunternif.mc.atlas.item.RecipeAtlasCombining;
import hunternif.mc.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.atlas.network.GlobalMarkersPacket;
import hunternif.mc.atlas.network.MapDataPacket;
import hunternif.mc.atlas.network.MarkersPacket;
import hunternif.mc.atlas.network.PacketPipeline;
import hunternif.mc.atlas.network.TileNameIDPacket;
import hunternif.mc.atlas.network.TilesPacket;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid=AntiqueAtlasMod.ID, name=AntiqueAtlasMod.NAME, version=AntiqueAtlasMod.VERSION)
public class AntiqueAtlasMod {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";
	public static final String CHANNEL = ID;
	public static final String VERSION = "4.0.1a-1.7.10";
	
	@Instance(ID)
	public static AntiqueAtlasMod instance;
	
	public static Logger logger;
	
	@SidedProxy(clientSide="hunternif.mc.atlas.ClientProxy", serverSide="hunternif.mc.atlas.CommonProxy")
	public static CommonProxy proxy;
	
	public static final PacketPipeline packetPipeline = new PacketPipeline();
	
	public static final RecipeAtlasCombining recipeCombining = new RecipeAtlasCombining();
	
	public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
	public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();
	
	public static ItemAtlas itemAtlas;
	public static ItemEmptyAtlas itemEmptyAtlas;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
		
		itemAtlas = (ItemAtlas) new ItemAtlas().setUnlocalizedName("antiqueAtlas");
		itemAtlas.setBiomeAnalyzer(new ChunkBiomeAnalyzer());
		
		itemEmptyAtlas = (ItemEmptyAtlas) new ItemEmptyAtlas()
			.setUnlocalizedName("emptyAntiqueAtlas").setCreativeTab(CreativeTabs.tabTools);
		
		GameRegistry.registerItem(itemAtlas, "antiqueAtlas");
		GameRegistry.registerItem(itemEmptyAtlas, "emptyAntiqueAtlas");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		packetPipeline.initialize();
		packetPipeline.registerPacket(MapDataPacket.class);
		packetPipeline.registerPacket(TilesPacket.class);
		packetPipeline.registerPacket(TileNameIDPacket.class);
		packetPipeline.registerPacket(MarkersPacket.class);
		packetPipeline.registerPacket(GlobalMarkersPacket.class);
		proxy.init(event);
		
		GameRegistry.addShapelessRecipe(new ItemStack(itemEmptyAtlas), Items.book, Items.compass);
		GameRegistry.addRecipe(new RecipeAtlasCloning());
		GameRegistry.addRecipe(recipeCombining);
		FMLCommonHandler.instance().bus().register(recipeCombining);
		
		MinecraftForge.EVENT_BUS.register(extBiomeData);
		FMLCommonHandler.instance().bus().register(extBiomeData);
		
		MinecraftForge.EVENT_BUS.register(globalMarkersData);
		FMLCommonHandler.instance().bus().register(globalMarkersData);
		
		MinecraftForge.EVENT_BUS.register(new VillageWatcher());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		packetPipeline.postInitialize();
		proxy.postInit(event);
	}
}
