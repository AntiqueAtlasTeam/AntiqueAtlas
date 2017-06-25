package hunternif.mc.atlas;

import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;
import hunternif.mc.atlas.item.RecipeAtlasCloning;
import hunternif.mc.atlas.item.RecipeAtlasCombining;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod.EventBusSubscriber(modid = AntiqueAtlasMod.ID)
@GameRegistry.ObjectHolder(AntiqueAtlasMod.ID)
public class RegistrarAntiqueAtlas {

    @GameRegistry.ObjectHolder("empty_antique_atlas")
    public static final ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas();
    @GameRegistry.ObjectHolder("antique_atlas")
    public static final ItemAtlas ATLAS = new ItemAtlas();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        if (SettingsConfig.gameplay.itemNeeded) {
            event.getRegistry().register(new ItemEmptyAtlas().setRegistryName("empty_antique_atlas"));
            event.getRegistry().register(new ItemAtlas().setRegistryName("antique_atlas"));
        }
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new ShapelessOreRecipe(new ResourceLocation(AntiqueAtlasMod.ID, "atlas"), new ItemStack(EMPTY_ATLAS), Items.BOOK, Items.COMPASS).setRegistryName("atlas_blank"));
        event.getRegistry().register(new RecipeAtlasCloning().setRegistryName("atlas_clone"));
        event.getRegistry().register(new RecipeAtlasCombining().setRegistryName("atlas_combine"));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        if (SettingsConfig.gameplay.itemNeeded) {
            ModelLoader.setCustomModelResourceLocation(EMPTY_ATLAS, 0, new ModelResourceLocation(EMPTY_ATLAS.getRegistryName(), "inventory"));
            ModelLoader.setCustomMeshDefinition(ATLAS, stack -> new ModelResourceLocation(ATLAS.getRegistryName(), "inventory"));
        }
    }

    // Probably not needed since Forge for 1.12 does not support transfers from earlier than 1.11.2, but just in case
    @SubscribeEvent
    public static void handleMissingMapping(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
            if (mapping.key.getResourcePath().equalsIgnoreCase("antiqueatlas"))
                mapping.remap(ATLAS);
            else if (mapping.key.getResourcePath().equalsIgnoreCase("emptyantiqueatlas"))
                mapping.remap(EMPTY_ATLAS);
        }
    }
}
