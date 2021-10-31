package hunternif.mc.impl.atlas.event;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Callback right when a item is crafted using a recipe.
 * <p>
 * Upon return:
 * - SUCCESS cancels further processing and, on the client, sends a packet to the server.
 * - PASS falls back to further processing.
 * - FAIL cancels further processing and does not send a packet to the server.
 */
public interface RecipeCraftedCallback {
	InteractionResult onCrafted(Player player, Level world, Recipe<?> recipe, ItemStack result, Container ingredients);
	
	public static void register(RecipeCraftedCallback consumer) {
		MinecraftForge.EVENT_BUS.addListener((Consumer<TheEvent>)event->consumer.onCrafted(event.getPlayer(), event.getLevel(), event.getRecipeUsed(), event.getCraftedStack(), event.getCraftSlots()));
	}
	/**
	 * This Event also takes the CraftingResultSlot
	 * @author Stereowalker
	 *
	 */
	public class TheEvent extends PlayerEvent {//TODO: ItemCraftedEvent
	    @Nonnull
	    private final Level level; 
	    private final Recipe<?> recipeUsed; 
	    private final ItemStack craftedStack; 
	    private final CraftingContainer craftSlots;

	    public TheEvent(Player player, Level level, Recipe<?> recipeUsed, ItemStack stack, CraftingContainer craftSlots) {
	    	super(player);
	    	this.level = level;
	    	this.recipeUsed = recipeUsed;
	    	this.craftedStack = stack;
	    	this.craftSlots = craftSlots;
	    }

		@Nonnull
	    public ItemStack getCraftedStack()
	    {
	        return this.craftedStack;
	    }
		
		public Recipe<?> getRecipeUsed() {
			return recipeUsed;
		}
		
		public CraftingContainer getCraftSlots() {
			return craftSlots;
		}
		
		public Level getLevel() {
			return level;
		}
	}
}
