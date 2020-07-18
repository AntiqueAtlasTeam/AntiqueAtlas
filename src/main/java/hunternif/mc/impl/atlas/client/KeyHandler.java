package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class KeyHandler {
    /** ID's of keys */
    private static final int KEY_ATLAS = 0;

    /** List of bindings (at this moment with only one binding) */
    private static List<FabricKeyBinding> bindings = new ArrayList<>(1);

    public static void registerBindings() {
        // Initialisation of bindings
        bindings.add(KEY_ATLAS, FabricKeyBinding.Builder.create(new Identifier("antiqueatlas:openatlas"), InputUtil.Type.KEYSYM, 77, "key.antiqueatlas.category").build());

        // Registering all binding
        bindings.forEach(KeyBindingRegistry.INSTANCE::register);
    }

    public static void onClientTick(MinecraftClient client) {
        if (bindings.get(KEY_ATLAS).wasPressed()) {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen instanceof GuiAtlas) {
                currentScreen.onClose();
            } else {
                GuiAtlas gui = new GuiAtlas();
                gui.updateL18n();
                client.openScreen(gui);
            }
        }
    }
}
