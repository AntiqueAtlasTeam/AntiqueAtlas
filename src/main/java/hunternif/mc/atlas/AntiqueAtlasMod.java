package hunternif.mc.atlas;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

import java.io.File;

import hunternif.mc.atlas.ext.DeathWatcher;
import hunternif.mc.atlas.ext.ExtBiomeDataHandler;
import hunternif.mc.atlas.ext.NetherFortressWatcher;
import hunternif.mc.atlas.ext.VillageWatcher;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;
import hunternif.mc.atlas.item.RecipeAtlasCloning;
import hunternif.mc.atlas.item.RecipeAtlasCombining;
import hunternif.mc.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.atlas.marker.NetherPortalWatcher;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.util.Log;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;
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
	public static final String VERSION = "4.2.6-1.7.10";
	
	@Instance(ID)
	public static AntiqueAtlasMod instance;
	
	@SidedProxy(clientSide="hunternif.mc.atlas.ClientProxy", serverSide="hunternif.mc.atlas.CommonProxy")
	public static CommonProxy proxy;
	
	public static final SettingsConfig settings = new SettingsConfig();
	
	public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
	public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();
	
	public static ItemAtlas itemAtlas;
	public static ItemEmptyAtlas itemEmptyAtlas;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Log.setModID(ID);
		proxy.preInit(event);
		settings.load(new File(proxy.configDir, "settings.cfg"));
		
		itemAtlas = (ItemAtlas) new ItemAtlas(settings).setUnlocalizedName("antiqueAtlas");
		
		itemEmptyAtlas = (ItemEmptyAtlas) new ItemEmptyAtlas()
			.setUnlocalizedName("emptyAntiqueAtlas").setCreativeTab(CreativeTabs.tabTools);
		
		GameRegistry.registerItem(itemAtlas, "antiqueAtlas");
		GameRegistry.registerItem(itemEmptyAtlas, "emptyAntiqueAtlas");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		PacketDispatcher.registerPackets();
		proxy.init(event);
		
		GameRegistry.addShapelessRecipe(new ItemStack(itemEmptyAtlas), Items.book, Items.compass);
		
		RecipeSorter.register("antiqueatlas:atlascloning",   RecipeAtlasCloning.class,   SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new RecipeAtlasCloning());
		
		RecipeSorter.register("antiqueatlas:atlascombining",   RecipeAtlasCombining.class,   SHAPELESS, "after:minecraft:shapeless");
		RecipeAtlasCombining recipeCombining = new RecipeAtlasCombining();
		GameRegistry.addRecipe(recipeCombining);
		FMLCommonHandler.instance().bus().register(recipeCombining);
		
		MinecraftForge.EVENT_BUS.register(extBiomeData);
		FMLCommonHandler.instance().bus().register(extBiomeData);
		
		MinecraftForge.EVENT_BUS.register(globalMarkersData);
		FMLCommonHandler.instance().bus().register(globalMarkersData);
		
		MinecraftForge.EVENT_BUS.register(new DeathWatcher());
		
		MinecraftForge.EVENT_BUS.register(new VillageWatcher());
		MinecraftForge.EVENT_BUS.register(new NetherFortressWatcher());
		MinecraftForge.EVENT_BUS.register(new NetherPortalWatcher());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
