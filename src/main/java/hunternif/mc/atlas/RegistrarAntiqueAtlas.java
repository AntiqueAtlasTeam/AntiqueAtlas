package hunternif.mc.atlas;

import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;
import hunternif.mc.atlas.item.RecipeAtlasCloning;
import hunternif.mc.atlas.item.RecipeAtlasCombining;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class RegistrarAntiqueAtlas {
    public static final ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas(new Item.Properties().group(ItemGroup.MISC));
    public static final ItemAtlas ATLAS = new ItemAtlas(new Item.Properties().maxStackSize(1));

    public static void register() {
        // TODO FABRIC
        // if (SettingsConfig.itemNeeded) {
        //}

    }
    public static void registerSerializers() {


        /* if (SettingsConfig.itemNeeded) {
            event.getRegistry().register(new ShapelessOreRecipe(new ResourceLocation(AntiqueAtlasMod.ID, "atlas"), new ItemStack(EMPTY_ATLAS), aud.XX_1_13_2_aS_XX, aud.XX_1_13_2_aX_XX).setRegistryName("atlas_blank"));
            event.getRegistry().register(new RecipeAtlasCloning().setRegistryName("atlas_clone"));
            event.getRegistry().register(new RecipeAtlasCombining().setRegistryName("atlas_combine"));
        } */
    }
}
