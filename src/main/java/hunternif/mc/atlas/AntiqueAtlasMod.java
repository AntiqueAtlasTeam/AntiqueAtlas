package hunternif.mc.atlas;

import hunternif.mc.atlas.client.KeyHandler;
import hunternif.mc.atlas.core.AtlasDataHandler;
import hunternif.mc.atlas.core.GlobalAtlasData;
import hunternif.mc.atlas.core.PlayerEventHandler;
import hunternif.mc.atlas.ext.ExtBiomeDataHandler;
import hunternif.mc.atlas.ext.watcher.DeathWatcher;
import hunternif.mc.atlas.ext.watcher.impl.StructureWatcherFortress;
import hunternif.mc.atlas.ext.watcher.impl.StructureWatcherVillage;
import hunternif.mc.atlas.item.RecipeAtlasCloning;
import hunternif.mc.atlas.item.RecipeAtlasCombining;
import hunternif.mc.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.atlas.marker.MarkersDataHandler;
import hunternif.mc.atlas.marker.NetherPortalWatcher;
import hunternif.mc.atlas.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AntiqueAtlasMod.ID)
public class AntiqueAtlasMod {
    public static final String ID = "antiqueatlas";
    public static final String NAME = "Antique Atlas";
    public static final String CHANNEL = ID;
    public static final String VERSION = "@VERSION@";

    public static AntiqueAtlasMod instance;
    public static Logger logger = LogManager.getLogger();

    public static CommonProxy proxy;

    public static final AtlasDataHandler atlasData = new AtlasDataHandler();
    public static final MarkersDataHandler markersData = new MarkersDataHandler();

    public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
    public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();

    //public static final RecipeCraftedHandler craftedHandler = new RecipeCraftedHandler();

    public AntiqueAtlasMod() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SettingsConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SettingsConfig.COMMON_SPEC);
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfigEvent);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    @SubscribeEvent
    public void onModConfigEvent(ModConfig.ModConfigEvent event) {
        AntiqueAtlasMod.logger.info("Got config event");
        if (event.getConfig().getSpec() == SettingsConfig.CLIENT_SPEC) {
            SettingsConfig.bakeConfigClient();
        }
        if (event.getConfig().getSpec() == SettingsConfig.COMMON_SPEC) {
            SettingsConfig.bakeConfigCommon();
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        instance = this;
        //proxy = new CommonProxy();

        //SettingsConfig.loadConfig();
        //PacketDispatcher.registerPacketsCommon();
        //PacketDispatcher.registerPacketsServer();
        PacketDispatcher.registerPackets();

        //proxy.init();


        if (!SettingsConfig.itemNeeded)
            MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(extBiomeData);

        MinecraftForge.EVENT_BUS.register(new DeathWatcher());

        MinecraftForge.EVENT_BUS.register(new NetherPortalWatcher());

        // Structure Watchers
        new StructureWatcherVillage();
        new StructureWatcherFortress();
        //new StructureWatcherGeneric("EndCity", XX_1_13_none_bnu_XX.c, MarkerTypes.END_CITY_FAR, "gui.antiqueatlas.marker.endcity").setTileMarker(MarkerTypes.END_CITY, "gui.antiqueatlas.marker.endcity")
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //AntiqueAtlasMod.proxy = new ClientProxy();
        //clientProxy.initClient();

        // TODO FABRIC hack
        // run twice -> register client-side packets too
        //PacketDispatcher.registerPacketsClient();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        logger.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void serverPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        globalMarkersData.onPlayerLogin((ServerPlayerEntity) event.getPlayer());
        extBiomeData.onPlayerLogin((ServerPlayerEntity) event.getPlayer());
        PlayerEventHandler.onPlayerLogin((ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        PlayerEventHandler.onPlayerTick(event.player);
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        KeyHandler.onClientTick();
    }

    @SubscribeEvent
    public void playerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            DeathWatcher.onPlayerDeath((PlayerEntity) event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void joinWorld(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerWorld) {
            globalMarkersData.onWorldLoad((ServerWorld) event.getWorld());
            extBiomeData.onWorldLoad((ServerWorld) event.getWorld());
        }
    }

    @SubscribeEvent
    public void recipeCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getInventory() instanceof IRecipeHolder) {
            onCrafted(event.getPlayer(), event.getPlayer().world, ((IRecipeHolder) (event.getInventory())).getRecipeUsed(), event.getCrafting(), event.getInventory());
        }
    }

    public ActionResultType onCrafted(PlayerEntity player, World world, IRecipe recipe, ItemStack result, IInventory
            ingredients) {
        if (world.isRemote()) return ActionResultType.PASS;

        if (recipe instanceof RecipeAtlasCombining) {
            RecipeAtlasCombining combining_recipe = (RecipeAtlasCombining) recipe;

            combining_recipe.onCrafted(world, ingredients, result);
        }

        return ActionResultType.PASS;
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            logger.info("HELLO from Register Block");
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            //RegistrarAntiqueAtlas.register();
            logger.info("HELLO from Register Item");
            itemRegistryEvent.getRegistry().register(RegistrarAntiqueAtlas.EMPTY_ATLAS.setRegistryName(new ResourceLocation("antiqueatlas:empty_antique_atlas")));
            itemRegistryEvent.getRegistry().register(RegistrarAntiqueAtlas.ATLAS.setRegistryName(new ResourceLocation("antiqueatlas:antique_atlas")));
        }

        @SubscribeEvent
        public static void onSerializerRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> serializerRegistryEvent) {
            //RegistrarAntiqueAtlas.registerSerializers();
            Registry.register(Registry.RECIPE_SERIALIZER, "antiqueatlas:atlas_clone", RecipeAtlasCloning.SERIALIZER);
            Registry.register(Registry.RECIPE_SERIALIZER, "antiqueatlas:atlas_combine", RecipeAtlasCombining.SERIALIZER);
        }
    }

    public static ResourceLocation id(String name) {
        if (name.indexOf(':') > 0) {
            return new ResourceLocation(name);
        } else {
            return new ResourceLocation("antiqueatlas", name);
        }
    }

    // TODO FABRIC cleanup
    private static final GlobalAtlasData clientAtlasData = new GlobalAtlasData("antiqueatlas:global_atlas_data");

    public static GlobalAtlasData getGlobalAtlasData(World world) {
        if (world instanceof ServerWorld) {
            return ((ServerWorld) world).getSavedData().getOrCreate(() -> new GlobalAtlasData("antiqueatlas:global_atlas_data"), "antiqueatlas:global_atlas_data");
        } else {
            return clientAtlasData;
        }
    }
}
