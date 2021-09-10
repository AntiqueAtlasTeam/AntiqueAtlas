package hunternif.mc.impl.atlas.event;

import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

/**
 * Callback right when a item is crafted using a recipe.
 * <p>
 * Upon return:
 * - SUCCESS cancels further processing and, on the client, sends a packet to the server.
 * - PASS falls back to further processing.
 * - FAIL cancels further processing and does not send a packet to the server.
 */
public interface RecipeCraftedCallback {

    Event<RecipeCraftedCallback> EVENT = EventFactory.createEventResult(RecipeCraftedCallback.class);

    ActionResult onCrafted(PlayerEntity player, World world, Recipe recipe, ItemStack result, Inventory ingredients);
}
