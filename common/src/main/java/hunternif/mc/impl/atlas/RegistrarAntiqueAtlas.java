package hunternif.mc.impl.atlas;

import dev.architectury.injectables.annotations.ExpectPlatform;
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

    @ExpectPlatform
    public static void register() {
        // Just throw an error, the content should get replaced at runtime.
        // Something is terribly wrong if this is not replaced.
        throw new AssertionError();
    }
}
