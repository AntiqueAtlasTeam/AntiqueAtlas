package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class KeyHandler {
    /** ID's of keys */
    private static final int KEY_ATLAS = 0;

    private static final KeyBinding openAtlas = new KeyBinding("key.openatlas", InputMappings.Type.KEYSYM, 77, "key.antiqueatlas.category");

    /** List of bindings (at this moment with only one binding) */
    //private static List<KeyBinding> bindings = new ArrayList<>(1);

    public static void registerBindings() {
        // Initialisation of bindings

        // Registering all binding
        //bindings.forEach(KeyBindingRegistry.INSTANCE::register);
    }

    public static void onClientTick() {
        if (openAtlas.isPressed()) {
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            if (currentScreen instanceof GuiAtlas) {
                currentScreen.onClose();
            } else {
                AntiqueAtlasMod.proxy.openAtlasGUI();
            }
        }
    }
}
