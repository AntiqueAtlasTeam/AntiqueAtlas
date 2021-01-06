//package hunternif.mc.impl.atlas.event;
//
//import net.fabricmc.fabric.api.event.Event;
//import net.fabricmc.fabric.api.event.EventFactory;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.Inventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.ActionResultType;
//import net.minecraft.world.World;
//
///**
// * Callback right when a item is crafted using a recipe.
// * <p>
// * Upon return:
// * - SUCCESS cancels further processing and, on the client, sends a packet to the server.
// * - PASS falls back to further processing.
// * - FAIL cancels further processing and does not send a packet to the server.
// */
//public interface RecipeCraftedCallback {
//
//    FIXME Event<RecipeCraftedCallback> EVENT = EventFactory.createArrayBacked(RecipeCraftedCallback.class,
//            (listeners) -> (player, world, recipe, result_stack, ingredients) -> {
//                for (RecipeCraftedCallback event : listeners) {
//                    ActionResult result = event.onCrafted(player, world, recipe, result_stack, ingredients);
//                    if (result != ActionResult.PASS) {
//                        return result;
//                    }
//                }
//
//                return ActionResult.PASS;
//            }
//    );
//
//    ActionResultType onCrafted(PlayerEntity player, World world, IRecipe recipe, ItemStack result, Inventory ingredients);
//}
