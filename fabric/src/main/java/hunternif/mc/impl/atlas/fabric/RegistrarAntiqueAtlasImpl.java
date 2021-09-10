package hunternif.mc.impl.atlas.fabric;

import hunternif.mc.impl.atlas.core.PlayerEventHandler;
import hunternif.mc.impl.atlas.event.RecipeCraftedCallback;
import hunternif.mc.impl.atlas.event.RecipeCraftedHandler;
import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.item.ItemEmptyAtlas;
import hunternif.mc.impl.atlas.item.RecipeAtlasCloning;
import hunternif.mc.impl.atlas.item.RecipeAtlasCombining;
import hunternif.mc.impl.atlas.mixinhooks.NewPlayerConnectionCallback;
import hunternif.mc.impl.atlas.mixinhooks.NewServerConnectionCallback;
import hunternif.mc.impl.atlas.structure.StructureAddedCallback;
import hunternif.mc.impl.atlas.structure.StructureHandler;
import hunternif.mc.impl.atlas.structure.StructurePieceAddedCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static hunternif.mc.impl.atlas.AntiqueAtlasMod.*;

public class RegistrarAntiqueAtlasImpl {
    public static final ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas(new Item.Settings().group(ItemGroup.MISC));
    public static final AtlasItem ATLAS = new AtlasItem(new Item.Settings().maxCount(1));

    public static void register() {
        if (CONFIG.itemNeeded) {
            Registry.register(Registry.ITEM, new Identifier("antiqueatlas:empty_antique_atlas"), EMPTY_ATLAS);
            Registry.register(Registry.ITEM, new Identifier("antiqueatlas:antique_atlas"), ATLAS);

            Registry.register(Registry.RECIPE_SERIALIZER, "antiqueatlas:atlas_clone", RecipeAtlasCloning.SERIALIZER);
            Registry.register(Registry.RECIPE_SERIALIZER, "antiqueatlas:atlas_combine", RecipeAtlasCombining.SERIALIZER);
        }
    }
}
