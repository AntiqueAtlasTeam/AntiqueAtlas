package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AntiqueAtlasModClient {
	private static GuiAtlas guiAtlas;
	private static GuiAtlas getAtlasGUI() {
		if (guiAtlas == null) {
			guiAtlas = new GuiAtlas();
			guiAtlas.setMapScale(AntiqueAtlasConfig.defaultScale.get());
		}
		return guiAtlas;
	}
	public static void openAtlasGUI(ItemStack stack) {
		openAtlasGUI(getAtlasGUI().prepareToOpen(stack));
	}
	public static void openAtlasGUI() {
		openAtlasGUI(getAtlasGUI().prepareToOpen());
	}
	private static void openAtlasGUI(GuiAtlas gui) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.currentScreen == null) { // In-game screen
			guiAtlas.updateL18n();
			mc.displayGuiScreen(gui);
		}
	}
	
	public static void onInitializeClient() {
		ClientProxy clientProxy = new ClientProxy();
		clientProxy.initClient();

//		AntiqueAtlasNetworking.registerS2CListeners();
	}
}
