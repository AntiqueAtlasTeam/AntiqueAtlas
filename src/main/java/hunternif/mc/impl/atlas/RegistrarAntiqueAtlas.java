package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.item.ItemEmptyAtlas;
import hunternif.mc.impl.atlas.item.RecipeAtlasCloning;
import hunternif.mc.impl.atlas.item.RecipeAtlasCombining;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistrarAntiqueAtlas {
    public static final ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas(new Item.Settings().group(ItemGroup.MISC));
    public static final AtlasItem ATLAS = new AtlasItem(new Item.Settings().maxCount(1));

    public static void register() {
        if (AntiqueAtlasMod.CONFIG.itemNeeded) {
            Registry.register(Registry.ITEM, new Identifier("antiqueatlas:empty_antique_atlas"), EMPTY_ATLAS);
            Registry.register(Registry.ITEM, new Identifier("antiqueatlas:antique_atlas"), ATLAS);

            Registry.register(Registry.RECIPE_SERIALIZER, "antiqueatlas:atlas_clone", RecipeAtlasCloning.SERIALIZER);
            Registry.register(Registry.RECIPE_SERIALIZER, "antiqueatlas:atlas_combine", RecipeAtlasCombining.SERIALIZER);
        }
    }
}
