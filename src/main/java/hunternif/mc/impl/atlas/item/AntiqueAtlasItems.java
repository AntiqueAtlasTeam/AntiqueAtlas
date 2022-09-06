package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AntiqueAtlasItems {
    public static final EmptyAtlasItem EMPTY_ATLAS = new EmptyAtlasItem(new Item.Settings().group(ItemGroup.MISC));
    public static final AtlasItem ATLAS = new AtlasItem(new Item.Settings().maxCount(1));

    public static ItemStack getAtlasFromId(int atlasID) {
        ItemStack atlas = new ItemStack(ATLAS);
        atlas.getOrCreateNbt().putInt("atlasID", atlasID);

        return atlas;
    }

    public static void register() {
        if (AntiqueAtlasMod.CONFIG.itemNeeded) {
            Registry.register(Registry.ITEM, new Identifier("antiqueatlas:empty_antique_atlas"), EMPTY_ATLAS);
            Registry.register(Registry.ITEM, new Identifier("antiqueatlas:antique_atlas"), ATLAS);

            Registry.register(Registry.RECIPE_SERIALIZER, "antiqueatlas:atlas_clone", RecipeAtlasCloning.SERIALIZER);
            Registry.register(Registry.RECIPE_SERIALIZER, "antiqueatlas:atlas_combine", RecipeAtlasCombining.SERIALIZER);
        }
    }
}
