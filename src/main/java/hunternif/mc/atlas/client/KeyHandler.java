package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class KeyHandler {
    /** ID's of keys */
    private static final int KEY_ATLAS = 0;

    /** List of bindings (at this moment with only one binding) */
    private static List<FabricKeyBinding> bindings = new ArrayList<>(1);

    public static void registerBindings() {
        // Initialisation of bindings
        bindings.add(KEY_ATLAS, FabricKeyBinding.Builder.create(new Identifier("antiqueatlas:openatlas"), InputUtil.Type.KEY_KEYBOARD, 77, "key.antiqueatlas.category").build());

        // Registering all binding
        bindings.forEach(KeyBindingRegistry.INSTANCE::register);
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if (bindings.get(KEY_ATLAS).wasPressed()) {
            XX_1_12_2_none_blk_XX currentScreen = MinecraftClient.getInstance().XX_1_12_2_m_XX;
            if (currentScreen instanceof GuiAtlas) {
                ((GuiAtlas) currentScreen).close();
            } else {
                AntiqueAtlasMod.proxy.openAtlasGUI();
            }
        }
    }
}
