package hunternif.mc.impl.atlas;

import dev.architectury.injectables.annotations.ExpectPlatform;
import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.item.ItemEmptyAtlas;
import hunternif.mc.impl.atlas.item.RecipeAtlasCloning;
import hunternif.mc.impl.atlas.item.RecipeAtlasCombining;
import me.shedaniel.architectury.registry.DeferredRegister;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistrarAntiqueAtlas {
    public static final DeferredRegister<Item>
            ITEMS = DeferredRegister.create(AntiqueAtlasMod.ID, Registry.ITEM_KEY);
    public static final DeferredRegister<RecipeSerializer<?>>
            RECIPES = DeferredRegister.create(AntiqueAtlasMod.ID, Registry.RECIPE_SERIALIZER_KEY);

    public static final RegistrySupplier<Item> EMPTY_ATLAS = ITEMS.register("empty_antique_atlas", () -> new ItemEmptyAtlas(new Item.Settings().group(ItemGroup.MISC)));
    public static final RegistrySupplier<Item> ATLAS = ITEMS.register("antique_atlas", () -> new AtlasItem(new Item.Settings().maxCount(1)));

    public static void register() {
        ITEMS.register();

        RECIPES.register("atlas_clone", () -> RecipeAtlasCloning.SERIALIZER);
        RECIPES.register("atlas_combine", () -> RecipeAtlasCombining.SERIALIZER);

        RECIPES.register();
    }
}
