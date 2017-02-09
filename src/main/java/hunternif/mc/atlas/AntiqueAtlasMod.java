package hunternif.mc.atlas;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;
import hunternif.mc.atlas.core.AtlasDataHandler;
import hunternif.mc.atlas.ext.DeathWatcher;
import hunternif.mc.atlas.ext.ExtBiomeDataHandler;
import hunternif.mc.atlas.ext.NetherFortressWatcher;
import hunternif.mc.atlas.ext.StructureWatcher;
import hunternif.mc.atlas.ext.VillageWatcher;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;
import hunternif.mc.atlas.item.RecipeAtlasCloning;
import hunternif.mc.atlas.item.RecipeAtlasCombining;
import hunternif.mc.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.atlas.marker.MarkersDataHandler;
import hunternif.mc.atlas.marker.NetherPortalWatcher;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerTypes;
import hunternif.mc.atlas.util.Log;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

@Mod(modid=AntiqueAtlasMod.ID, name=AntiqueAtlasMod.NAME, version=AntiqueAtlasMod.VERSION)
public class AntiqueAtlasMod {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";
	public static final String CHANNEL = ID;
	public static final String VERSION = "@VERSION@";
	
	@Instance(ID)
	public static AntiqueAtlasMod instance;
	
	@SidedProxy(clientSide="hunternif.mc.atlas.ClientProxy", serverSide="hunternif.mc.atlas.CommonProxy")
	public static CommonProxy proxy;
	
	public static final SettingsConfig settings = new SettingsConfig();
	
	public static final AtlasDataHandler atlasData = new AtlasDataHandler();
	public static final MarkersDataHandler markersData = new MarkersDataHandler();
	
	public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
	public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();
	
	public static ItemAtlas itemAtlas;
	public static ItemEmptyAtlas itemEmptyAtlas;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Log.setModID(ID);
		MarkerRegistry.INSTANCE.getClass(); // load the class
		MarkerTypes.INSTANCE.getClass(); // ...
		proxy.preInit(event);
		settings.load(new File(proxy.configDir, "settings.cfg"));
		
		itemAtlas = (ItemAtlas) new ItemAtlas(settings)
			.setRegistryName(ID, "antiqueAtlas").setUnlocalizedName("antiqueAtlas");
		
		itemEmptyAtlas = (ItemEmptyAtlas) new ItemEmptyAtlas()
			.setRegistryName(ID, "emptyAntiqueAtlas").setUnlocalizedName("emptyAntiqueAtlas")
			.setCreativeTab(CreativeTabs.TOOLS);
		
		GameRegistry.register(itemAtlas.setRegistryName("antiqueAtlas"));
		GameRegistry.register(itemEmptyAtlas.setRegistryName("emptyAntiqueAtlas"));
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		PacketDispatcher.registerPackets();
		proxy.init(event);
		
		GameRegistry.addShapelessRecipe(new ItemStack(itemEmptyAtlas), Items.BOOK, Items.COMPASS);
		
		RecipeSorter.register("antiqueatlas:atlascloning",   RecipeAtlasCloning.class,   SHAPELESS, "after:minecraft:shapeless");
		GameRegistry.addRecipe(new RecipeAtlasCloning());
		
		RecipeSorter.register("antiqueatlas:atlascombining",   RecipeAtlasCombining.class,   SHAPELESS, "after:minecraft:shapeless");
		RecipeAtlasCombining recipeCombining = new RecipeAtlasCombining();
		GameRegistry.addRecipe(recipeCombining);
		MinecraftForge.EVENT_BUS.register(recipeCombining);
		
		MinecraftForge.EVENT_BUS.register(atlasData);
		MinecraftForge.EVENT_BUS.register(markersData);
		
		MinecraftForge.EVENT_BUS.register(extBiomeData);
		
		MinecraftForge.EVENT_BUS.register(globalMarkersData);
		
		MinecraftForge.EVENT_BUS.register(new DeathWatcher());
		
		MinecraftForge.EVENT_BUS.register(new StructureWatcher("EndCity", 1, MarkerTypes.END_CITY_FAR, "gui.antiqueatlas.marker.endcity").setTileMarker(MarkerTypes.END_CITY, "gui.antiqueatlas.marker.endcity"));
		MinecraftForge.EVENT_BUS.register(new VillageWatcher());
		MinecraftForge.EVENT_BUS.register(new NetherFortressWatcher());
		MinecraftForge.EVENT_BUS.register(new NetherPortalWatcher());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
