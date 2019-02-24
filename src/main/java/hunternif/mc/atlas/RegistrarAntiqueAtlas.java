package hunternif.mc.atlas;

import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;
import hunternif.mc.atlas.item.RecipeAtlasCloning;
import hunternif.mc.atlas.item.RecipeAtlasCombining;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

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

    // TODO FABRIC itemgroups
    public static final ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas(new Item.Settings());
    public static final ItemAtlas ATLAS = new ItemAtlas(new Item.Settings().stackSize(1));

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        if (SettingsConfig.gameplay.itemNeeded) {
            event.getRegistry().register(new ItemEmptyAtlas().setRegistryName("empty_antique_atlas"));
            event.getRegistry().register(new ItemAtlas().setRegistryName("antique_atlas"));
        }
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<Recipe> event) {
        /* if (SettingsConfig.gameplay.itemNeeded) {
            event.getRegistry().register(new ShapelessOreRecipe(new Identifier(AntiqueAtlasMod.ID, "atlas"), new ItemStack(EMPTY_ATLAS), aud.XX_1_13_2_aS_XX, aud.XX_1_13_2_aX_XX).setRegistryName("atlas_blank"));
            event.getRegistry().register(new RecipeAtlasCloning().setRegistryName("atlas_clone"));
            event.getRegistry().register(new RecipeAtlasCombining().setRegistryName("atlas_combine"));
        } */
        // TODO FABRIC
    }
}
