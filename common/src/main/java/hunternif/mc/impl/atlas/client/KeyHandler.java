package hunternif.mc.impl.atlas.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import me.shedaniel.architectury.registry.KeyBindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;


import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class KeyHandler {
    /**
     * ID's of keys
     */
    private static final int KEY_ATLAS = 0;

    /**
     * List of bindings (at this moment with only one binding)
     */
    private static List<KeyBinding> bindings = new ArrayList<>(1);

    @ExpectPlatform
    public static void registerBindings() {
        // Initialisation of bindings
        bindings.add(KEY_ATLAS, new KeyBinding("key.openatlas.desc", InputUtil.Type.KEYSYM, 77, "key.antiqueatlas.category"));

        // Registering all binding
        bindings.forEach(KeyBindings::registerKeyBinding);
    }

    public static void onClientTick(MinecraftClient client) {
        if (bindings.get(KEY_ATLAS).wasPressed()) {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen instanceof GuiAtlas) {
                currentScreen.onClose();
            } else {
                AntiqueAtlasModClient.openAtlasGUI();
            }
        }
    }
}
