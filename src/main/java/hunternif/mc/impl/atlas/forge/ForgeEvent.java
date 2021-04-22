package hunternif.mc.impl.atlas.forge;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.client.KeyHandler;
import hunternif.mc.impl.atlas.client.OverlayRenderer;
import hunternif.mc.impl.atlas.client.gui.ExportProgressOverlay;
import hunternif.mc.impl.atlas.core.PlayerEventHandler;
import hunternif.mc.impl.atlas.event.RecipeCraftedHandler;
import hunternif.mc.impl.atlas.forge.event.ItemCraftedEvent;
import hunternif.mc.impl.atlas.forge.event.StructureAddedEvent;
import hunternif.mc.impl.atlas.forge.event.StructurePieceAddedEvent;
import hunternif.mc.impl.atlas.structure.StructureHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgeEvent {

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void login(ClientPlayerNetworkEvent.LoggedInEvent event) {
		AntiqueAtlasMod.tileData.onClientConnectedToServer(event.getPlayer().getEntityWorld().isRemote);
		AntiqueAtlasMod.markersData.onClientConnectedToServer(event.getPlayer().getEntityWorld().isRemote);
		AntiqueAtlasMod.globalMarkersData.onClientConnectedToServer(event.getPlayer().getEntityWorld().isRemote);
	}

	@SubscribeEvent
	public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
			AntiqueAtlasMod.globalMarkersData.onPlayerLogin(player);
			AntiqueAtlasMod.globalTileData.onPlayerLogin(player);
			PlayerEventHandler.onPlayerLogin(player);
		}
	}

	@SubscribeEvent
	public static void worldLoad(WorldEvent.Load event) {
		if (event.getWorld() instanceof ServerWorld) {
			ServerWorld server = (ServerWorld)event.getWorld();
			AntiqueAtlasMod.globalMarkersData.onWorldLoad(server.getServer(), server);
			AntiqueAtlasMod.globalTileData.onWorldLoad(server.getServer(), server);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void clientTick(ClientTickEvent event) {
		if (!AntiqueAtlasConfig.itemNeeded.get()) {
			KeyHandler.onClientTick(Minecraft.getInstance());
		}
	}

//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent
//	public static void in(RenderGameOverlayEvent.Pre event) {
//		//************************************************//
//		if (event.getType() == ElementType.CROSSHAIRS) {
//			if (Minecraft.getInstance().player != null) {
//				for (ItemStack stack : Minecraft.getInstance().player.getHeldEquipment()) {
//					if (stack.getItem() == Items.FILLED_MAP || stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
//						event.setCanceled(true);
//					}
//				}
//			}
//
////			Minecraft.getInstance().ingameGUI.func_238456_d_(event.getMatrixStack());
//		}
//	}
	
//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent
//	public static void in(RenderGameOverlayEvent.Post event) {
//		//************************************************//
//		if (AntiqueAtlasConfig.enabled.get() && event.getType() == ElementType.ALL) {
//			event.getMatrixStack().push();
//			event.getMatrixStack().translate(AntiqueAtlasConfig.xPosition.get(), AntiqueAtlasConfig.yPosition.get(), 0);
//			event.getMatrixStack().scale(
//					0.3F,
//					0.3F,
//					1F
//					);
//			new OverlayRenderer().drawOverlay(event.getMatrixStack());
//			event.getMatrixStack().pop();
//		}
//		//************************************************//
//		ExportProgressOverlay.INSTANCE.draw(event.getMatrixStack(), Minecraft.getInstance().ingameGUI.scaledWidth, Minecraft.getInstance().ingameGUI.scaledHeight);
//	}
	
	@SubscribeEvent
	public static void playerTick(LivingUpdateEvent event) {
		if (event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEventHandler.onPlayerTick((PlayerEntity) event.getEntityLiving());
		}
	}
	
	@SubscribeEvent
	public static void re(StructureAddedEvent event) {
		StructureHandler.resolve(event.getStructureStart(), event.getWorld());
	}
	
	@SubscribeEvent
	public static void rec(StructurePieceAddedEvent event) {
		StructureHandler.resolve(event.getStructurePiece(), event.getWorld());
	}
	
	@SubscribeEvent
	public static void craftRecipe(ItemCraftedEvent event) {
		if (event.getResultSlot().inventory instanceof IRecipeHolder) {
			RecipeCraftedHandler.onCrafted(event.getPlayer(), event.getPlayer().getEntityWorld(), ((IRecipeHolder)event.getResultSlot().inventory).getRecipeUsed(), event.getCraftedStack(), event.getCraftingMatrix());
		}
	}
}
