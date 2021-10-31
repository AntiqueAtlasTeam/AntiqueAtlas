package hunternif.mc.impl.atlas;

import java.util.function.Consumer;

import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.item.ItemEmptyAtlas;
import hunternif.mc.impl.atlas.item.RecipeAtlasCloning;
import hunternif.mc.impl.atlas.item.RecipeAtlasCombining;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RegistrarAntiqueAtlas {
    public static final ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final AtlasItem ATLAS = new AtlasItem(new Item.Properties().stacksTo(1));

    public static void register() {
        if (AntiqueAtlasMod.CONFIG.itemNeeded) {
			final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
			modEventBus.addGenericListener(RecipeSerializer.class, (Consumer<RegistryEvent.Register<RecipeSerializer<?>>>)event-> {
				event.getRegistry().register(RecipeAtlasCloning.SERIALIZER.setRegistryName(new ResourceLocation("antiqueatlas:atlas_clone")));
				event.getRegistry().register(RecipeAtlasCombining.SERIALIZER.setRegistryName(new ResourceLocation("antiqueatlas:atlas_combine")));
			});
			modEventBus.addGenericListener(Item.class, (Consumer<RegistryEvent.Register<Item>>)event-> {
				event.getRegistry().register(ATLAS.setRegistryName(new ResourceLocation("antiqueatlas:antique_atlas")));
				event.getRegistry().register(EMPTY_ATLAS.setRegistryName(new ResourceLocation("antiqueatlas:empty_antique_atlas")));
			});
        }
    }
}
