package hunternif.mc.atlas;

import hunternif.mc.atlas.client.GuiAtlas;
import hunternif.mc.atlas.core.PlayerInfo;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.src.ModLoader;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class AtlasKeyHandler extends KeyHandler {
	public static final String KEY_DESCRIPTION_ATLAS = "antiqueatlas.key.atlas"; 
	
	public static final KeyBinding bindingJournal = new KeyBinding(KEY_DESCRIPTION_ATLAS, Keyboard.KEY_M);
	
	public AtlasKeyHandler() {
		super(new KeyBinding[]{bindingJournal}, new boolean[]{false});
	}

	@Override
	public String getLabel() {
		return "Antique Atlas Key Handler";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (tickEnd) {
			if (kb == bindingJournal) {
				Minecraft mc = Minecraft.getMinecraft();
				if (mc.currentScreen == null) { // In-game screen
					PlayerInfo info = AntiqueAtlasMod.playerTracker.getPlayerInfo(mc.thePlayer);
					ModLoader.openGUI(mc.thePlayer, new GuiAtlas(info));
				} else if (mc.currentScreen instanceof GuiAtlas) {
					mc.thePlayer.closeScreen();
				}
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
