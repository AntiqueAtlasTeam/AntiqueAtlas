package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.item.ItemEmptyAtlas;
import hunternif.mc.impl.atlas.item.RecipeAtlasCloning;
import hunternif.mc.impl.atlas.item.RecipeAtlasCombining;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = AntiqueAtlasMod.ID)
public class RegistrarAntiqueAtlas
{
	public static final ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas(new Item.Properties().group(ItemGroup.MISC));
	public static final AtlasItem ATLAS = new AtlasItem(new Item.Properties().maxStackSize(1));


	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		if (AntiqueAtlasConfig.itemNeeded.get()) {
			event.getRegistry().register(ATLAS.setRegistryName(new ResourceLocation("antiqueatlas:antique_atlas")));
			event.getRegistry().register(EMPTY_ATLAS.setRegistryName(new ResourceLocation("antiqueatlas:empty_antique_atlas")));
		}
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
		if (AntiqueAtlasConfig.itemNeeded.get()) {
			event.getRegistry().register(RecipeAtlasCloning.SERIALIZER.setRegistryName(new ResourceLocation("antiqueatlas:atlas_clone")));
			event.getRegistry().register(RecipeAtlasCombining.SERIALIZER.setRegistryName(new ResourceLocation("antiqueatlas:atlas_combine")));
		}

	}
}
