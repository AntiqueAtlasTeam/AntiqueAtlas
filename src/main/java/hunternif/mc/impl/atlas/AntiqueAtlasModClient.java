package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AntiqueAtlasModClient {

	private static GuiAtlas guiAtlas;

	public static GuiAtlas getAtlasGUI() {
		if (guiAtlas == null) {
			guiAtlas = new GuiAtlas();
			guiAtlas.setMapScale(AntiqueAtlasMod.CONFIG.defaultScale);
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
		if (mc.screen == null) { // In-game screen
			guiAtlas.updateL18n();
			mc.setScreen(gui);
		}
	}
}
