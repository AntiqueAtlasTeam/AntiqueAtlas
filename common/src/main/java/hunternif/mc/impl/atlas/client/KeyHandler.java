package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import me.shedaniel.architectury.registry.KeyBindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;


@Environment(EnvType.CLIENT)
public class KeyHandler {
    public static final KeyBinding ATLAS_KEYMAPPING = new KeyBinding("key.openatlas.desc", InputUtil.Type.KEYSYM, 77, "key.antiqueatlas.category");

    public static void registerBindings() {
        KeyBindings.registerKeyBinding(ATLAS_KEYMAPPING);
    }

    public static void onClientTick(MinecraftClient client) {
        while (ATLAS_KEYMAPPING.wasPressed()) {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen instanceof GuiAtlas) {
                currentScreen.onClose();
            } else {
                AntiqueAtlasModClient.openAtlasGUI();
            }
        }
    }
}
